package main

import (
	"bytes"
	_ "bytes"
	"control"
	"crypto/sha512"
	"database/sql"
	"encoding/json"
	"fmt"
	"io/ioutil"
	_ "io/ioutil"
	"net/http"
	"os"
	_ "os"
	"strconv"
	"time"

	"github.com/gorilla/mux"
	_ "github.com/gorilla/mux"
	_ "github.com/mattn/go-sqlite3"
	"github.com/stretchr/gomniauth"
	_ "github.com/stretchr/gomniauth"
	"github.com/stretchr/gomniauth/providers/github"
	"github.com/stretchr/objx"
)

type emptyStruct struct {
}

const QUANTITY = 1
const ADD_EDIT = 2
const DELETE = 4

type Item struct {
	Item     int     `json:"item"`
	Name     string  `json:"name"`
	Prod     string  `json:"prod"`
	Price    float32 `json:"price"`
	Quantity int     `json:"quantity"`
}

type Quantity struct {
	Item  int `json:"item"`
	Delta int `json:"change"`
}

type Action struct {
	Item		int			`json:"item"`
	Name		string		`json:"name"`
	Prod    	string 		`json:"prod"`
	Price   	float32		`json:"price"`
	Quantity	int   		`json:"quantity"`
	Action		string		`json:"action"`
}

type Email struct {
	Email      string `json:"email"`
	Verified   bool   `json:"verified"`
	Primary    bool   `json:"primary"`
	Visibility string `json:"visibility"`
}

var tokens map[string]int = make(map[string]int)
var db *sql.DB

func getUserLevel(user int) int {
	var err error

	stmt, err := db.Prepare("select level from users where id=?")
	if err != nil {
		fmt.Println(err.Error())
		return 0
	}
	defer stmt.Close()

	var lev int
	err = stmt.QueryRow(user).Scan(&lev)
	if err != nil {
		fmt.Println(err.Error())
		return 0
	}
	return lev
}

func checkToken(t string) int {
	userid, ok := tokens[t]
	if ok {
		return userid
	}
	return 0
}

func get(id int) *Item {
	var item Item
	stmt, err := db.Prepare("select id, name, prod, quantity, price from items where id=?")
	if err != nil {
		fmt.Println(err.Error())
		fmt.Println("<get> Error preparing")
		return nil
	}
	err = stmt.QueryRow(id).Scan(&(item.Item), &(item.Name), &(item.Prod), &(item.Quantity), &(item.Price))
	if err != nil {
		fmt.Println(err.Error())
		fmt.Println("<get> Error query")
		return nil
	}

	return &item
}

func add(item *Item) int {
	fmt.Printf(">Adding item %s, %s, %.2f, %d\n", item.Prod, item.Name, item.Price, item.Quantity)
	stmt, err := db.Prepare("insert into items(name, prod, quantity, price) values(?,?,?,?)")
	if err != nil {
		fmt.Println(err.Error())
		fmt.Println("<add> Error preparing")
		return -1
	}
	r, err := stmt.Exec(item.Name, item.Prod, item.Quantity, item.Price)
	if err != nil {
		fmt.Println(err.Error())
		fmt.Println("<add> Error executing")
		return -1
	}
	lastid, err := r.LastInsertId()
	if err != nil {
		fmt.Println(err.Error())
		fmt.Println("<add> Last insert error")
		return int(lastid)
	}
	fmt.Printf(">Added item at #%d", lastid)
	return int(lastid)
}

func delete(item int) int {
	fmt.Printf(">Deleting item %d\n", item)
	stmt, err := db.Prepare("delete from items where id = ?")
	if err != nil {
		fmt.Println(err.Error())
		fmt.Println("<del> Error preparing")
		return -1
	}
	_, err = stmt.Exec(item)
	if err != nil {
		fmt.Printf(err.Error())
		fmt.Println("<del> Error executing")
		return -1
	}
	return 200
}

func update(item *Item) int {
	id := item.Item
	_item := get(id)
	if _item == nil {
		fmt.Printf("<upd> No item with id = %d\n", id)
		return 404
	}
	stmt, err := db.Prepare("update items set name=?, prod=?, price=? where id=?")
	if err != nil {
		fmt.Println(err.Error())
		fmt.Println("<upd> Error preparing")
		return -1
	}
	_, err = stmt.Exec(item.Name, item.Prod, item.Price, id)
	if err != nil {
		fmt.Println(err.Error())
		fmt.Println("<upd> Error executing")
		return -1
	}
	return 200
}

func quantity(item int, delta int) int {
	r, err := db.Exec("INSERT INTO deltas (item, delta) VALUES (?, ?)", item, delta)
	if err != nil {
		fmt.Println(err.Error())
		fmt.Println("<qun> Error executing")
		return -1
	}
	rowsa, err := r.RowsAffected()
	if err != nil {
		fmt.Println(err.Error())
		fmt.Println("<qun> Unknown rows affected")
		return 202
	}
	if rowsa <= 0 {
		fmt.Println("<qun> Warn: no rows affected")
		return 404
	}
	return 200
}

func httpsync(w http.ResponseWriter, r *http.Request) {
	
	switch r.Method {
		case "OPTIONS":
			w.Header().Set("Access-Control-Allow-Origin", "*")
			w.Header().Set("Content-Type", "application/json")
			w.Header().Set("Access-Control-Allow-Headers", "*")
			w.Header().Set("Access-Control-Allow-Methods", "*")
			break

		case "POST":
			w.Header().Add("Access-Control-Allow-Origin", "*")
			w.Header().Add("Content-type", "application/json")
			token := r.Header.Get("X-Auth-Token")
			uid := checkToken(token)
			if uid <= 0 {
				w.WriteHeader(http.StatusUnauthorized)
				fmt.Println("Authorize: Unauthorized", token)
				return
			}

			level := getUserLevel(uid)
			
			
			
			w.WriteHeader(http.StatusOK)
			w.Write([]byte(fmt.Sprintf("{}")))
			break
		default:
			w.WriteHeader(http.StatusMethodNotAllowed)
			w.Header().Add("Access-Control-Allow-Origin", "*")
	}
	
}

func httplist(w http.ResponseWriter, r *http.Request) {
	w.Header().Add("Access-Control-Allow-Origin", "*")
	w.Header().Add("Content-type", "application/json")

	rows, err := db.Query("select id, name, prod, price, quantity from items")
	if err != nil {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusInternalServerError)
		return
	}
	defer rows.Close()

	items := []Item{}

	for rows.Next() {
		var item Item
		err = rows.Scan(&(item.Item), &(item.Name), &(item.Prod), &(item.Price), &(item.Quantity))
		if err != nil {
			fmt.Println(err.Error())
			w.WriteHeader(http.StatusInternalServerError)
			return
		}
		subrows, err := db.Prepare("SELECT COALESCE(SUM(delta),0) FROM deltas WHERE item = ?")
		if err != nil {
			fmt.Println(err.Error())
			w.WriteHeader(http.StatusInternalServerError)
			return
		}
		var delta int64
		err = subrows.QueryRow(item.Item).Scan(&delta)
		if err != nil {
			fmt.Println(err.Error())
			w.WriteHeader(http.StatusInternalServerError)
			return
		}
		item.Quantity += int(delta)
		items = append(items, item)
	}

	for _, it := range items {
		fmt.Printf(">> #%d is %s, %s\n", it.Item, it.Prod, it.Name)
	}

	itemjson, err := json.Marshal(items)
	if err != nil {
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusInternalServerError)
		return
	}

	w.WriteHeader(http.StatusOK)
	w.Write(itemjson)
}

func httpadd(w http.ResponseWriter, r *http.Request) {
	w.Header().Add("Access-Control-Allow-Origin", "*")
	w.Header().Add("Content-type", "application/json")
	token := r.Header.Get("X-Auth-Token")
	uid := checkToken(token)
	if uid <= 0 {
		w.WriteHeader(http.StatusUnauthorized)
		fmt.Println("Unauthorized")
		return
	}

	level := getUserLevel(uid)
	if level < ADD_EDIT {
		w.WriteHeader(http.StatusForbidden)
		fmt.Println("Forbidden")
		return
	}

	body, err := ioutil.ReadAll(r.Body)
	if err != nil {
		w.WriteHeader(http.StatusBadRequest)
		fmt.Println("Bad request")
		return
	}
	var item *Item = new(Item)
	err = json.NewDecoder(bytes.NewReader(body)).Decode(item)
	if err != nil {
		w.WriteHeader(http.StatusBadRequest)
		fmt.Println("Bad request (json)")
		return
	}
	newid := add(item)
	w.WriteHeader(http.StatusCreated)
	fmt.Fprintf(w, "{\"id\":\"%d\"}", newid)
	fmt.Printf("[%d : %d] Added new item id = %d\n", uid, level, newid)
}

func httpdelete(w http.ResponseWriter, r *http.Request) {
	w.Header().Add("Access-Control-Allow-Origin", "*")
	w.Header().Add("Content-type", "application/json")
	token := r.Header.Get("X-Auth-Token")
	uid := checkToken(token)
	if uid <= 0 {
		w.WriteHeader(http.StatusUnauthorized)
		fmt.Println("Unauthorized")
		return
	}

	level := getUserLevel(uid)
	if level < DELETE {
		w.WriteHeader(http.StatusForbidden)
		fmt.Println("Forbidden")
		return
	}

	vars := mux.Vars(r)

	itemid, err := strconv.Atoi(vars["itemid"])
	if err != nil {
		w.WriteHeader(http.StatusBadRequest)
		fmt.Println("Bad request (atoi)")
		return
	}
	ret := delete(itemid)
	if ret < 0 {
		w.WriteHeader(http.StatusInternalServerError)
		fmt.Println("Internal error")
		return
	}
	w.WriteHeader(ret)
}

func httpupdate(w http.ResponseWriter, r *http.Request) {
	w.Header().Add("Access-Control-Allow-Origin", "*")
	w.Header().Add("Content-type", "application/json")
	token := r.Header.Get("X-Auth-Token")
	uid := checkToken(token)
	if uid <= 0 {
		w.WriteHeader(http.StatusUnauthorized)
		fmt.Println("Unauthorized")
		return
	}

	level := getUserLevel(uid)
	if level < ADD_EDIT {
		w.WriteHeader(http.StatusForbidden)
		fmt.Println("Forbidden")
		return
	}

	body, err := ioutil.ReadAll(r.Body)
	if err != nil {
		w.WriteHeader(http.StatusBadRequest)
		fmt.Println("Bad request")
		return
	}
	var item *Item = new(Item)
	err = json.NewDecoder(bytes.NewReader(body)).Decode(item)
	if err != nil {
		w.WriteHeader(http.StatusBadRequest)
		fmt.Println("Bad request (json)")
		return
	}

	ret := update(item)
	if ret < 0 {
		w.WriteHeader(http.StatusInternalServerError)
		fmt.Println("Internal error")
		return
	}
	w.WriteHeader(ret)
}

func httpchange(w http.ResponseWriter, r *http.Request) {
	w.Header().Add("Access-Control-Allow-Origin", "*")
	w.Header().Add("Content-type", "application/json")
	token := r.Header.Get("X-Auth-Token")
	uid := checkToken(token)
	if uid <= 0 {
		w.WriteHeader(http.StatusUnauthorized)
		fmt.Println("Unauthorized")
		return
	}

	level := getUserLevel(uid)
	if level < QUANTITY {
		w.WriteHeader(http.StatusForbidden)
		fmt.Println("Forbidden")
		return
	}

	vars := mux.Vars(r)

	itemid, err := strconv.Atoi(vars["itemid"])
	if err != nil {
		w.WriteHeader(http.StatusBadRequest)
		fmt.Println("Bad request (atoi)")
		return
	}

	body, err := ioutil.ReadAll(r.Body)
	if err != nil {
		w.WriteHeader(http.StatusBadRequest)
		fmt.Println("Bad request")
		return
	}
	var item *Quantity = new(Quantity)
	err = json.NewDecoder(bytes.NewReader(body)).Decode(item)
	if err != nil {
		w.WriteHeader(http.StatusBadRequest)
		fmt.Println("Bad request (json)")
		return
	}

	ret := quantity(itemid, item.Delta)
	if ret < 0 {
		w.WriteHeader(http.StatusInternalServerError)
		fmt.Println("Internal error")
	}
	fmt.Printf("Changed quantity %d of item %d\n", item.Delta, itemid)
	w.WriteHeader(ret)
}

const TEST_TOKEN = "aabbccddeeffgghhiijjkkllmmnn"

func main() {
	var err error
	db, err = sql.Open("sqlite3", "./msi.db")
	if err != nil {
		fmt.Println(err.Error())
		return
	}
	defer db.Close()

	testToken := newToken("rotworm0@yahoo.pl")
	tokens[testToken] = 1

	fmt.Printf("Created test token %s\n", testToken)

	gomniauth.SetSecurityKey(os.Getenv("OAUTH_SECURITY_KEY"))
	gomniauth.WithProviders(
		github.New(os.Getenv("GITHUB_CLIENT_ID"), os.Getenv("GITHUB_SECRET_KEY"), fmt.Sprintf("https://%s:8000/callback/github", os.Getenv("MSI_HOSTNAME"))),
	)

	rtr := mux.NewRouter()
	http.Handle("/", rtr)

	rtr.HandleFunc("/", listItems)
	rtr.HandleFunc("/authorize", httpauth)
	rtr.HandleFunc("/sync", httpsync)
	rtr.HandleFunc("/callback/{prov}", callback)
	rtr.HandleFunc("/login/{prov}", login)
	rtr.HandleFunc("/{itemid}", item)

	http.ListenAndServeTLS(":8000", "server.crt", "server.key", nil)
}

func login(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	prov := vars["prov"]

	w.Header().Add("Access-Control-Allow-Origin", "*")

	provider, err := gomniauth.Provider(prov)
	if err != nil {
		http.Error(w, "Provider unsupported.", http.StatusBadRequest)
		fmt.Println("Provider unsupported.")
		return
	}

	loginUrl, err := provider.GetBeginAuthURL(nil, nil)
	if err != nil {
		http.Error(w, "Begin url unknown", http.StatusInternalServerError)
		fmt.Println("Begin url unknown")
		return
	}
	w.Header().Set("Location", loginUrl)
	w.WriteHeader(http.StatusTemporaryRedirect)
}

func callback(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	prov := vars["prov"]

	w.Header().Add("Access-Control-Allow-Origin", "*")

	provider, err := gomniauth.Provider(prov)
	if err != nil {
		http.Error(w, "Provider unsupported.", http.StatusBadRequest)
		fmt.Printf("Provider unsupported %s\n", err.Error())
		return
	}

	fmt.Printf("Debug: %s\n", r.URL.RawQuery)
	creds, err := provider.CompleteAuth(objx.MustFromURLQuery(r.URL.RawQuery))
	if err != nil {
		fmt.Printf("Error getting credentials %s\n", err.Error())
		http.Error(w, "Error getting credentials.", http.StatusInternalServerError)
		return
	}

	client, err := provider.GetClient(creds)
	if err != nil {
		http.Error(w, "Error getting authenticated client.", http.StatusInternalServerError)
		fmt.Printf("Error getting auth user %s\n", err.Error())
		return
	}

	resp, err := client.Get("https://api.github.com/user/emails")
	if err != nil {
		http.Error(w, "Error asking for emails", http.StatusInternalServerError)
		fmt.Printf("Error asking for emails %s\n", err.Error())
		return
	}

	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		http.Error(w, "Error reading emails", http.StatusInternalServerError)
		return
	}

	emails := []Email{}
	err = json.Unmarshal(body, &emails)
	if err != nil {
		http.Error(w, "Error decoding emails (json)", http.StatusInternalServerError)
		fmt.Println(err.Error())
		return
	}

	if len(emails) <= 0 {
		http.Error(w, "No emails found!", http.StatusBadRequest)
		return
	}

	if len(emails[0].Email) < 5 {
		http.Error(w, "Email record is not valid!", http.StatusBadRequest)
		return
	}

	if !emails[0].Verified {
		http.Error(w, "Email unverified!", http.StatusUnauthorized)
		return
	}

	_, token, err := loginWithEmail(emails[0].Email)
	if err != nil {
		http.Error(w, "Error logging in", http.StatusInternalServerError)
		return
	}

	http.Redirect(w, r, fmt.Sprintf("http://%s/?token=%s", os.Getenv("MSI_APPNAME"), token), 301)
}

func newToken(email string) string {
	bytes := []byte(fmt.Sprintf("%s%s", email, time.Now().String()))
	h := sha512.New()
	h.Write(bytes)
	return fmt.Sprintf("%X", h.Sum(nil))
}

func loginWithEmail(email string) (int, string, error) {
	stmt, err := db.Prepare("select count(*) from users where email=?")
	if err != nil {
		return -1, "", err
	}

	var count int
	err = stmt.QueryRow(email).Scan(&count)
	if err != nil {
		return -1, "", err
	}

	if count > 0 {
		stmt, err = db.Prepare("select id from users where email=?")
		if err != nil {
			return -1, "", err
		}

		var id int
		err = stmt.QueryRow(email).Scan(&id)
		if err != nil {
			return -1, "", err
		}

		token := newToken(email)
		tokens[token] = id
		fmt.Printf("Logged in %s #%d with token %s...\n", email, id, token[:6])
		return id, token, nil

	} else {
		stmt, err = db.Prepare("insert into users(email, level) values(?,0)")
		if err != nil {
			return -1, "", err
		}
		r, err := stmt.Exec(email)
		if err != nil {
			return -1, "", err
		}

		id, err := r.LastInsertId()
		if err != nil {
			return -1, "", err
		}

		token := newToken(email)
		tokens[token] = int(id)
		fmt.Printf("Created and logged in %s #%d with token %s...\n", email, int(id), token[:6])

		return int(id), token, nil

	}

	return -1, "", nil
}

func listItems(w http.ResponseWriter, r *http.Request) {

	switch r.Method {
	case "OPTIONS":
		w.Header().Set("Access-Control-Allow-Origin", "*")
		w.Header().Set("Content-Type", "application/json")
		w.Header().Set("Access-Control-Allow-Headers", "*")
		w.Header().Set("Access-Control-Allow-Methods", "*")
		break

	case "POST":
		httpadd(w, r)
		break

	case "GET":
		fmt.Printf("GET httplist\n")
		httplist(w, r)
		break

	case "PUT":
		httpupdate(w, r)
		break

	case "HEAD":
		length := control.ListItems(nil)
		w.Header().Add("Access-Control-Allow-Origin", "*")
		w.Header().Add("Content-Type", "application/json")
		w.Header().Add("Content-Length", strconv.Itoa(length))
		break

	default:
		w.Header().Add("Access-Control-Allow-Origin", "*")
		w.WriteHeader(http.StatusMethodNotAllowed)

	}
}

func item(w http.ResponseWriter, r *http.Request) {

	switch r.Method {
	case "OPTIONS":
		w.Header().Set("Access-Control-Allow-Origin", "*")
		w.Header().Set("Content-Type", "application/json")
		w.Header().Set("Access-Control-Allow-Headers", "*")
		w.Header().Set("Access-Control-Allow-Methods", "*")
		break

	case "DELETE":
		httpdelete(w, r)
		break

	case "PUT":
		httpchange(w, r)
		break

	default:
		w.WriteHeader(http.StatusMethodNotAllowed)
		w.Header().Add("Access-Control-Allow-Origin", "*")
	}
}

func httpauth(w http.ResponseWriter, r *http.Request) {
	switch r.Method {
	case "OPTIONS":
		w.Header().Set("Access-Control-Allow-Origin", "*")
		w.Header().Set("Content-Type", "application/json")
		w.Header().Set("Access-Control-Allow-Headers", "*")
		w.Header().Set("Access-Control-Allow-Methods", "*")
		break

	case "GET":
		w.Header().Add("Access-Control-Allow-Origin", "*")
		w.Header().Add("Content-type", "application/json")
		token := r.Header.Get("X-Auth-Token")
		uid := checkToken(token)
		if uid <= 0 {
			w.WriteHeader(http.StatusUnauthorized)
			fmt.Println("Authorize: Unauthorized", token)
			return
		}

		level := getUserLevel(uid)
		w.WriteHeader(http.StatusOK)
		w.Write([]byte(fmt.Sprintf("{\"level\":\"%d\"}", level)))
		break
	default:
		w.WriteHeader(http.StatusMethodNotAllowed)
		w.Header().Add("Access-Control-Allow-Origin", "*")
	}
}
