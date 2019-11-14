package main

import (
	"bytes"
	_ "bytes"
	"control"
	"database/sql"
	"encoding/json"
	"fmt"
	"io/ioutil"
	_ "io/ioutil"
	"net/http"
	"strconv"

	"github.com/gorilla/mux"
	_ "github.com/gorilla/mux"
	_ "github.com/mattn/go-sqlite3"
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
	fmt.Println(r)
	return 200
}

func delete(item int) int {
	return 200
}

func update(item *Item) int {
	return 200
}

func quantity(item *Item) int {
	return 200
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
	w.WriteHeader(http.StatusOK)
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

	tokens[TEST_TOKEN] = 1

	fmt.Println("Created test token ", TEST_TOKEN)

	rtr := mux.NewRouter()
	http.Handle("/", rtr)

	rtr.HandleFunc("/", listItems)
	rtr.HandleFunc("/{itemid}", item)

	http.ListenAndServe(":8000", nil)
}

func listItems(w http.ResponseWriter, r *http.Request) {

	switch r.Method {
	case "POST":
		httpadd(w, r)

	case "GET":
		fmt.Printf("GET httplist\n")
		httplist(w, r)

	case "HEAD":
		length := control.ListItems(nil)
		w.Header().Add("Access-Control-Allow-Origin", "*")
		w.Header().Add("Content-Type", "application/json")
		w.Header().Add("Content-Length", strconv.Itoa(length))

	default:
		w.Header().Add("Access-Control-Allow-Origin", "*")
		w.WriteHeader(http.StatusMethodNotAllowed)

	}
}

func item(w http.ResponseWriter, r *http.Request) {

	vars := mux.Vars(r)

	switch r.Method {
	case "GET":
		err := control.ViewItem(vars, w)
		if err != nil {
			w.WriteHeader(http.StatusBadRequest)
			w.Header().Add("Access-Control-Allow-Origin", "*")
			w.Header().Add("Content-Type", "application/json")
			fmt.Fprintf(w, "{\"error\":\"%s\"}", err.Error())
		}

	case "PUT":
		err := control.UpdateItem(r, vars)
		if err != nil {
			w.WriteHeader(http.StatusBadRequest)
			w.Header().Add("Access-Control-Allow-Origin", "*")
			w.Header().Add("Content-Type", "application/json")
			fmt.Fprintf(w, "{\"error\":\"%s\"}", err.Error())
		}

	default:
		w.WriteHeader(http.StatusMethodNotAllowed)
		w.Header().Add("Access-Control-Allow-Origin", "*")
	}
}
