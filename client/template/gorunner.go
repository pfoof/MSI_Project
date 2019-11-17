package main

import (
	"fmt"
	"net/http"
	_ "os"
)

func main() {
	http.HandleFunc("/", serve)

	http.ListenAndServeTLS(":5500", "../../server/server.crt", "../../server/server.key", nil)
}

func serve(w http.ResponseWriter, r *http.Request) {
	fmt.Println(r.URL.Path)
	p := "." + r.URL.Path
	if p == "./" || p == "." {
		p = "./index.html"
	}
	http.ServeFile(w, r, p)
}
