package client.views;

import priori.view.layout.PriHorizontalLayout;
import haxe.Json;
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
    private var layout: PriHorizontalLayout;

    override function setup() {
        super.setup();
        layout = new PriHorizontalLayout();
        layout.alignType = CENTER;
        editButton = new PriBSImage();
        editButton.load("images/edit.png");
        deleteButton = new PriBSImage();
        deleteButton.load("images/delete.png");
        editButton.addEventListener(PriTapEvent.TAP, editEvent);
        deleteButton.addEventListener(PriTapEvent.TAP, deleteEvent);
        layout.addChildList([editButton, deleteButton]);
        this.addChild(layout);
        update();
        validate();
    }

    override function update() {
        super.update();
        var val:ItemListActionRendererModel = Json.parse(value);

        if(editButton == null || deleteButton == null) return;

        editButton.visible = false;
        deleteButton.visible = false;

        if(val.actions == null) return;

        if(val.actions.indexOf(Constants.ACTION_DELETE) > -1) {
            deleteButton.visible = true;
        }
        
        if(val.actions.indexOf(Constants.ACTION_EDIT) > -1) {
            editButton.visible = true;
        }
    }

    override function paint() {
        super.paint();
        layout.x = layout.y = 0;
        layout.width = this.width;
        layout.height = this.height;
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

class ItemListActionRendererModel {

    public var name: String;
    public var prod: String;
    public var quantity: Int;
    public var price: Float;
    public var id: Int;
    public var actions: Array<String>;

    public function new(name: String, prod: String, quantity: String, price: String, id: String, actions: Array<String>) {
        this.name = name;
        this.prod = prod;
        this.quantity = Std.parseInt(quantity);
        this.price = Std.parseFloat(price);
        this.id = Std.parseInt(id);
        this.actions = actions;
    }
}