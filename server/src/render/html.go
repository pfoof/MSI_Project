package render

import (
	"html/template"
	"mydatabase"
	"net/http"
)

func RenderIndex(e []mydatabase.Entity, wr http.ResponseWriter, r *http.Request) {
	t, _ := template.ParseFiles("templates/index.html")
	page := struct {
		Entities []mydatabase.Entity
	}{
		e,
	}
	t.Execute(wr, page)
}
