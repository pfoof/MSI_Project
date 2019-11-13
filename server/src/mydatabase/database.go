package mydatabase

import (
	"fmt"
)

var Entities []Entity = make([]Entity, 0)
var Users map[uint]int = make(map[uint]int)
var NextId uint = 1

/*
	generates a test database
*/
func TestDatabase() {
	var ent Entity
	ent.Name = "htc"
	ent.Prod = "hermes"
	ent.Price = 182.12
	AddNew(ent)
}

/*
	@e - entity to be added
*/
func AddNew(e Entity) uint {
	e.ID = NextId
	NextId++
	Entities = append(Entities, e)
	return NextId - 1
}

/*
	@e - entity to be updated
*/
func Update(e Entity) error {
	e, i, err := FindEntity(e.ID)
	if err != nil {
		return err
	}

	Entities[i].Name = e.Name
	Entities[i].Prod = e.Prod

	return nil
}

/*
	@id - id to be searched

	entity - entity searched
	error - in case of error
*/
func FindEntity(id uint) (Entity, int, error) {
	for index, element := range Entities {
		if element.ID == id {
			return element, index, nil
		}
	}
	return Entity{}, -1, fmt.Errorf("element %u not found", id)
}

func GetUserLevel(id uint) int {

	if val, ok := Users[id]; ok {
		return val
	}

	return 1
}
