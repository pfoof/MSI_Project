package main

import "encoding/json"

type Entity struct {
	ID   uint   `json:"id"`
	Name string `json:"name"`
	Prod string `json:"prod"`
}
