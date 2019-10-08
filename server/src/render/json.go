package render

import (
	"encoding/json"
	"mydatabase"
	"net/http"
)

func RenderJson(e []mydatabase.Entity, wr http.ResponseWriter) {
	wr.Header().Add("Content-type", "application/json")
	data, err := json.Marshal(e)
	if err != nil {
		wr.WriteHeader(http.StatusTeapot)
		return
	}
	wr.Write(data)
}
