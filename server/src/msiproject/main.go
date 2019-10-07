package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"github.com/gorilla/mux"
	"mydatabase"
	"net/http"
)

func listItems(w http.ResponseWriter, r *http.Request) {

	if r.Method == "POST" {
		item(w, r)
		return
	} else if r.Method == "GET" {
		fmt.Fprintf(w, "\tID\tManufacturer\tName")
		for i, e := range mydatabase.Entities {
			fmt.Fprintf(w, "%d:\t%d\t%s\t%s", i, e.ID, e.Prod, e.Name)
		}
	} else if r.Method == "HEAD" {
		w.Header().Add("Content-Length", "1024")
		w.Header().Add("Content-Type", "text/plain")
	} else {
		w.WriteHeader(http.StatusBadRequest)
	}

}

func item(w http.ResponseWriter, r *http.Request) {

	var buffer []byte

	_, error := r.Body.Read(buffer)
	if error != nil {
		w.WriteHeader(http.StatusBadRequest)
		fmt.Fprintf(w, error.Error())
		return
	}

	var ent mydatabase.Entity

	error2 := json.NewDecoder(bytes.NewReader(buffer)).Decode(ent)

	if error2 != nil {
		w.WriteHeader(http.StatusBadRequest)
		fmt.Fprintf(w, error.Error())
		return
	}

	//Add new item
	if r.Method == "POST" {
		newid := mydatabase.AddNew(ent)
		w.WriteHeader(http.StatusCreated)
		fmt.Fprintf(w, "{\"id\":\"%d\"}", newid)
		return
	} else if r.Method == "PUT" { //Edit existing
		error3 := mydatabase.Update(ent)
		if error3 != nil {
			w.WriteHeader(http.StatusConflict)
			fmt.Fprintf(w, error.Error())
			return
		}
	} else if r.Method == "GET" {

	}
}

func main() {
	rtr := mux.NewRouter()
	http.Handle("/", rtr)

	rtr.HandleFunc("/", listItems)
	rtr.HandleFunc("/{[0-9]+}", item)

	http.ListenAndServe(":8000", nil)
}
