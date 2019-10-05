package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"github.com/gorilla/mux"
	"net/http"
)

func editItem(w http.ResponseWriter, r *http.Request) {

	var buffer []byte

	_, error := r.Body.Read(buffer)
	if error != nil {
		w.WriteHeader(http.StatusBadRequest)
		fmt.Fprintf(w, error.Error())
		return
	}

	var ent Entity

	error2 := json.NewDecoder(bytes.NewReader(buffer)).Decode(ent)

	if error2 != nil {
		w.WriteHeader(http.StatusBadRequest)
		fmt.Fprintf(w, error.Error())
		return
	}

	//Edit an existing item
	if r.Method == "POST" {
		Update(ent)
	} else if r.Method == "PUT" { //Add a new item
		error3 := Update(ent)
		if error3 != nil {
			w.WriteHeader(http.StatusConflict)
			fmt.Fprintf(w, error.Error())
			return
		}
	} else if r.Method == "GET" {

	}
}

func main() {
	http.HandleFunc("/", editItem)
}
