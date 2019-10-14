package render

import (
	"encoding/json"
	"fmt"
	"mydatabase"
	"net/http"
)

func RenderJson(e []mydatabase.Entity, wr http.ResponseWriter) int {
	data, err := json.Marshal(e)
	if err != nil {
		if wr != nil {
			wr.WriteHeader(http.StatusInternalServerError)
		}
		return -1
	}
	if wr != nil {
		fmt.Fprintf(wr, string(data))
	}
	return len(data)
}
