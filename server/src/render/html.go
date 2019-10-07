package render

import (
	"fmt"
	"html/template"
	"mydatabase"
	"net/http"
	"os"
)

func RenderIndex(e []mydatabase.Entity, wr http.ResponseWriter, r *http.Request) {
	t, _ := template.ParseFiles(fmt.Sprintf("%s/templates/index.html", os.Getenv("GOPATH")))
	page := struct {
		Entities []mydatabase.Entity
	}{
		e,
	}
	t.Execute(wr, page)
}
