package client.views;

import client.Access.Signal;
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

class ItemList extends PriGroupWithState {

    public static inline final NAME = "itemlist";

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

        list = new PriDataGrid();
        list.columns = [
            new PriGridColumn("ID", "id", PriGridColumnSizeType.FIXED, 48, true),
            new PriGridColumn("Manufacturer", "prod", PriGridColumnSizeType.FIT, false),
            new PriGridColumn("Product", "name", PriGridColumnSizeType.FIT, false),
            new PriGridColumn("Price", "price", PriGridColumnSizeType.FIXED, 54, true)
        ];
        list.data = [{
            id: 100,
            prod: "htc",
            name: "polaris",
            price: 160.99
        }];
        addChild(list);

        addChild(refreshButton);

        Access.registerCallback(accessCallback);

        refresh();
    }

    private function accessCallback(signal: Signal, data: Dynamic): Void {
        switch(signal) {
            case Add | Delete | Quantity | Edit: {

            }
            case Retrieve: {
                onLoad(data);
            }
            default: {}
        }
    }

    override function paint() {
        super.paint();

        refreshButton.x = 48;
        refreshButton.y = 48;

        list.x = 48;
        list.width = this.width - 48*2;
        list.y = 24;
        list.height = this.height - list.y - 24;
    }

    public function refresh(?e: PriEvent): Void {
        trace("Starting request");
        Access.getAccessTarget().retrieveItems("");
    }

    public function onLoad(e: Dynamic): Void {
        var dataString: String = cast(cast(e.data, PriURLLoader).data, String);
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