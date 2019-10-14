package main

import (
	"control"
	"fmt"
	"github.com/gorilla/mux"
	"net/http"
	"strconv"
)

type emptyStruct struct {
}

func listItems(w http.ResponseWriter, r *http.Request) {

	switch r.Method {
	case "POST":
		w.Header().Add("Access-Control-Allow-Origin", "*")
		control.AddItem(r)

	case "GET":
		w.Header().Add("Access-Control-Allow-Origin", "*")
		w.Header().Add("Content-type", "application/json")
		control.ListItems(w)

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

func main() {
	rtr := mux.NewRouter()
	http.Handle("/", rtr)

	rtr.HandleFunc("/", listItems)
	rtr.HandleFunc("/{itemid}", item)

	http.ListenAndServe(":8000", nil)
}
