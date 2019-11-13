package client.views;

import priori.event.PriTapEvent;
import priori.event.PriEvent;
import priori.bootstrap.PriBSImage;
import priori.view.grid.cell.PriGridCellRenderer;

class ItemListActionRenderer extends PriGridCellRenderer {

    private var id: Int;
    private var name: String;
    private var prod: String;
    private var price: Float;
    private var quantity: Int;

    private var editButton: PriBSImage;
    private var deleteButton: PriBSImage;

    override function setup() {
        super.setup();
        editButton = new PriBSImage();
        editButton.load("images/edit.png");
        deleteButton = new PriBSImage();
        deleteButton.load("images/delete.png");
        editButton.addEventListener(PriTapEvent.TAP, editEvent);
        deleteButton.addEventListener(PriTapEvent.TAP, deleteEvent);
    }

    override function update() {
        super.update();

    }

    override function paint() {
        super.paint();

    }

    private function editEvent(e: PriEvent): Void {
        ContentManager.getManager()
            .switchContent(AddItemForm.NAME, [
                Constants.RESET_FORM_DATA_NAME => this.name,
                Constants.RESET_FORM_DATA_PROD => this.prod,
                Constants.RESET_FORM_DATA_QUANTITY => ""+this.quantity,
                Constants.RESET_FORM_DATA_PRICE => ""+this.price,
                Constants.RESET_FORM_DATA_ID => ""+this.id
            ]);
    }

    private function deleteEvent(e: PriEvent): Void {

    }

}