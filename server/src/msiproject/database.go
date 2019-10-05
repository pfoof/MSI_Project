package main

import (
	"fmt"
)

var Entities []Entity
var NextId uint = 1

/*
	@e - entity to be added
*/
func AddNew(e Entity) {
	e.ID = NextId
	NextId++
	append(Entities, e)
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
