package client.views;

import js.Browser;
import haxe.Json;
import priori.bootstrap.PriBSFormInputText;
import priori.net.PriRequestMethod;
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
    private var insertButton: PriBSFormButton;
    private var insertProd: PriBSFormInputText;
    private var insertName: PriBSFormInputText;

    public function new() {
        super();
    }

    override function setup() {
        super.setup();

        refreshButton = new PriBSFormButton();
        refreshButton.text = "Refresh";
        refreshButton.addEventListener(PriTapEvent.TAP, refresh);

        addChild(refreshButton);

        insertButton = new PriBSFormButton();
        insertButton.text = "Insert";
        insertButton.addEventListener(PriTapEvent.TAP, insert);
        insertProd = new PriBSFormInputText();
        insertProd.placeholder = "Manufacturer";
        insertName = new PriBSFormInputText();
        insertName.placeholder = "Product";

        addChild(insertName);
        addChild(insertProd);
        addChild(insertButton);

        list = new PriDataGrid();
        list.columns = [
            new PriGridColumn("ID", "id", PriGridColumnSizeType.FIXED, 48, true),
            new PriGridColumn("Manufacturer", "prod", PriGridColumnSizeType.FIT, false),
            new PriGridColumn("Product", "name", PriGridColumnSizeType.FIT, false)
        ];
        list.data = [{
            id: 100,
            prod: "htc",
            name: "polaris"
        }];
        addChild(list);

        refresh();
    }

    override function paint() {
        super.paint();

        refreshButton.x = 48;
        refreshButton.y = insertButton.y = insertProd.y = insertName.y = 48;
        insertButton.x = this.width - 48 - insertButton.width;
        var inputWidth = (insertButton.x - refreshButton.maxX - 3 * 24) / 2;
        insertProd.x = 24 + refreshButton.maxX;
        insertProd.width = inputWidth;
        insertName.x = 24 + insertProd.maxX;
        insertName.width = inputWidth;

        list.x = 48;
        list.width = this.width - 48*2;
        list.y = 24 + refreshButton.maxY;
        list.height = this.height - list.y - 48;
    }

    public function refresh(?e: PriEvent): Void {
        trace("Starting request");
        var request: PriURLRequest = new PriURLRequest(Constants.SERVER_DEST);
        request.method = PriRequestMethod.GET;
        var loader: PriURLLoader = new PriURLLoader();
        loader.addEventListener(PriEvent.COMPLETE, onLoad);
        loader.load(request);
    }

    public function insert(e: PriEvent): Void {
        Access.getAccessTarget().addProduct("", insertName.value, insertProd.value, 0.0, 1);
        requester.addEventListener(PriEvent.COMPLETE, onInsert);
    }

    public function onInsert(e: PriEvent): Void {
        refresh();
    }

    public function onLoad(e: PriEvent): Void {
        var dataString: String = e.data;
        list.data = haxe.Json.parse(dataString);
        trace(dataString);
    }

    public function itemAction(item: Int, change: Int) {
        if(Constants.hasConnection()) {
            var t = Access.getAccessTarget();
        } else {
            var log = Log.getLog();
            log.logQuantity(Constants.getTimestamp(), item, change);
            Log.saveLog(log);
        }
    }



}