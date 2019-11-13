package mydatabase

type Entity struct {
	ID   uint   `json:"id"`
	Name string `json:"name"`
	Prod string `json:"prod"`
	Price float32 `json:"price"`
}
