package client;

import priori.event.PriTapEvent;
import priori.net.PriURLLoader;
import priori.net.PriURLRequest;
import priori.view.grid.column.PriGridColumnSize;
import priori.view.grid.column.PriGridColumn;
import priori.view.grid.PriDataGrid;
import priori.view.container.PriGroup;
import priori.event.PriEvent;
import priori.bootstrap.PriBSFormButton;

class ItemList extends PriGroup {
    
    private var list: PriDataGrid;
    private var refreshButton: PriBSFormButton;

    public function new() {
        super();
    }

    override function setup() {
        super.setup();

        refreshButton = new PriBSFormButton();
        refreshButton.text = "Refresh";
        refreshButton.addEventListener(PriTapEvent.TAP, refresh);

        addChild(refreshButton);

        list = new PriDataGrid();
        list.columns = [
            new PriGridColumn("ID", "id", PriGridColumnSizeType.FIXED, 48, true),
            new PriGridColumn("Manufacturer", "prod", PriGridColumnSizeType.FIT, false),
            new PriGridColumn("Product", "name", PriGridColumnSizeType.FIT, false)
        ];
        addChild(list);

        refresh();
    }

    override function paint() {
        super.paint();

        refreshButton.x = 48;
        refreshButton.y = 48;

        list.x = 48;
        list.width = this.width - 48*2;
        list.y = 24 + refreshButton.maxY;
        list.height = this.height - list.y - 48;
    }

    public function refresh(?e: PriEvent): Void {
        var request: PriURLRequest = new PriURLRequest("http://localhost:8000/");
        var loader: PriURLLoader = new PriURLLoader();
        loader.addEventListener(PriEvent.COMPLETE, onLoad);
        loader.load(request);
    }

    public function onLoad(e: PriEvent): Void {
        var dataString: String = e.data;
        list.data = haxe.Json.parse(dataString);
    }

    public function addItem(item: ItemModel) {

    }

}