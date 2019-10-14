package control

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"mydatabase"
	"net/http"
	"strconv"
)

func ParseItemJson(r *http.Request) (mydatabase.Entity, error) {

	buffer, err := ioutil.ReadAll(r.Body)
	if err != nil {
		return mydatabase.Entity{}, fmt.Errorf("ParseItemJson %s", err.Error())
	}

	var ent *mydatabase.Entity = new(mydatabase.Entity)

	err2 := json.NewDecoder(bytes.NewReader(buffer)).Decode(ent)

	if err2 != nil {
		return mydatabase.Entity{}, fmt.Errorf("ParseItemJson %s", err2.Error())
	}

	return *ent, nil
}

func AddItem(r *http.Request) (int, error) {
	ent, err := ParseItemJson(r)
	if err != nil {
		return -1, fmt.Errorf("AddItem %s", err.Error())
	}
	return int(mydatabase.AddNew(ent)), nil

}

func UpdateItem(r *http.Request, vars map[string]string) error {
	var err error = nil

	id, err := strconv.Atoi(vars["itemid"])
	if err != nil {
		return fmt.Errorf("UpdateItem %s", err.Error())
	}

	if id <= 0 {
		return fmt.Errorf("UpdateItem id is too small")
	}

	ent, err := ParseItemJson(r)
	if err != nil {
		return fmt.Errorf("UpdateItem %s", err.Error())
	}

	ent.ID = uint(id)
	err = mydatabase.Update(ent)
	if err != nil {
		return fmt.Errorf("UpdateItem %s", err.Error())
	}

	return nil
}
