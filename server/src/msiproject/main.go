package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"github.com/gorilla/mux"
	"io/ioutil"
	"mydatabase"
	"net/http"
	"render"
	"strconv"
)

type emptyStruct struct {
}

func listItems(w http.ResponseWriter, r *http.Request) {

	if r.Method == "POST" {
		item(w, r)
		return
	} else if r.Method == "GET" {
		render.RenderJson(mydatabase.Entities, w)
	} else if r.Method == "HEAD" {
		w.Header().Add("Content-Length", "1024")
		w.Header().Add("Content-Type", "text/plain")
	} else {
		w.WriteHeader(http.StatusBadRequest)
	}

}

func item(w http.ResponseWriter, r *http.Request) {

	buffer, err := ioutil.ReadAll(r.Body)
	if err != nil {
		w.WriteHeader(http.StatusBadRequest)
		fmt.Fprintf(w, err.Error())
		return
	}

	var ent *mydatabase.Entity = new(mydatabase.Entity)

	err2 := json.NewDecoder(bytes.NewReader(buffer)).Decode(ent)

	if err2 != nil {
		w.WriteHeader(http.StatusBadRequest)
		fmt.Fprintf(w, err2.Error())
		return
	}

	//Add new item
	if r.Method == "POST" {
		newid := mydatabase.AddNew(*ent)
		w.WriteHeader(http.StatusCreated)
		fmt.Fprintf(w, "{\"id\":\"%d\"}", newid)
		return
	} else if r.Method == "PUT" { //Edit existing
		err3 := mydatabase.Update(*ent)
		if err3 != nil {
			w.WriteHeader(http.StatusConflict)
			fmt.Fprintf(w, err3.Error())
			return
		}
	} else if r.Method == "GET" {
		vars := mux.Vars(r)
		id, err4 := strconv.Atoi(vars["itemid"])
		if err4 != nil {
			w.WriteHeader(http.StatusBadRequest)
			fmt.Fprintf(w, err4.Error())
			return
		}
		ent, _, err5 := mydatabase.FindEntity(uint(id))
		if err5 != nil {
			w.WriteHeader(http.StatusNotFound)
			fmt.Fprintf(w, "{\"error\":\"Not found\"}")
			return
		}

		entjson, err6 := json.Marshal(ent)
		if err6 != nil {
			w.WriteHeader(http.StatusInternalServerError)
			fmt.Fprintf(w, "{\"error\":\"JSON processing error\"}")
			return
		}
		w.WriteHeader(200)
		fmt.Fprintf(w, string(entjson))
	}
}

func main() {
	rtr := mux.NewRouter()
	http.Handle("/", rtr)

	rtr.HandleFunc("/", listItems)
	rtr.HandleFunc("/{itemid}", item)

	http.ListenAndServe(":8000", nil)
}
