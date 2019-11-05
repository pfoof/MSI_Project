package control

import (
	"encoding/json"
	"fmt"
	"mydatabase"
	"net/http"
	"render"
	"strconv"
)

func ListItems(w http.ResponseWriter) int {
	return render.RenderJson(mydatabase.Entities, w)
}

func ViewItem(vars map[string]string, w http.ResponseWriter) error {
	var err error = nil

	id, err := strconv.Atoi(vars["itemid"])
	if err != nil {
		return fmt.Errorf("ViewItem %s", err.Error())
	}

	ent, _, err := mydatabase.FindEntity(uint(id))
	if err != nil {
		w.WriteHeader(http.StatusNotFound)
		return fmt.Errorf("ViewItem %s", err.Error())
	}

	entjson, err := json.Marshal(ent)
	if err != nil {
		return fmt.Errorf("ViewItem %s", err.Error())
	}

	w.WriteHeader(http.StatusOK)
	w.Header().Add("Content-Type", "application/json")
	fmt.Fprintf(w, string(entjson))

	return nil
}
