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
    private var toolbuttons: Toolbuttons;

    public function new() {
        super();
    }

    override function setup() {
        super.setup();

        toolbuttons = new Toolbuttons();
        refreshButton = new PriBSFormButton();
        refreshButton.text = "Refresh";
        refreshButton.addEventListener(PriTapEvent.TAP, refresh);

        list = new PriDataGrid();
        list.columns = [
            new PriGridColumn("ID", "item", PriGridColumnSizeType.FIXED, 48, true),
            new PriGridColumn("Manufacturer", "prod", PriGridColumnSizeType.FIT, false),
            new PriGridColumn("Product", "name", PriGridColumnSizeType.FIT, false),
            new PriGridColumn("Price", "price", PriGridColumnSizeType.FIXED, 54, true),
            new PriGridColumn("Quantity", "quantity", PriGridColumnSizeType.FIXED, 48, true),
            new PriGridColumn("", "actions", ItemListActionRenderer, PriGridColumnSizeType.FIXED, 64, false)
        ];
        list.data = [{
            item: 100,
            prod: "htc",
            name: "polaris",
            price: 160.99,
            quantity: 32,
            actions: {
                item:100,
                prod: "htc",
                name: "polaris",
                price: 160.99,
                quantity: 32,
                actions: ["edit"]
            }
        }];
        addChild(list);

        addChild(refreshButton);
        
        addChild(toolbuttons);

        Access.registerCallback(accessCallback);

        refresh();

        this.validate();
    }

    private function accessCallback(signal: Signal, data: Dynamic): Void {
        switch(signal) {
            case Add | Delete | Quantity | Edit: {
                refresh();
            }
            case Retrieve: {
                onLoad(data);
                trace("Retrieve!");
            }
            default: {}
        }
    }

    override function paint() {
        super.paint();

        toolbuttons.x = 0;
        toolbuttons.height = 64;
        toolbuttons.width = this.width;
        toolbuttons.y = this.height - toolbuttons.height;

        refreshButton.x = 48;
        refreshButton.y = 48;

        list.x = 48;
        list.width = this.width - 48*2;
        list.y = 24;
        list.height = this.height - list.y - 24;
    }

    public function refresh(?e: PriEvent): Void {
        trace("Starting request");
        Access.getAccessTarget().retrieveItems(Utils.getToken());
    }

    private function addActions(data: Array<Dynamic>): Array<Dynamic> {
        var newData: Array<Dynamic> = new Array<Dynamic>();
        for(d in data) {
            newData.push(d);
        }
        return newData;
    }

    public function onLoad(e: Dynamic): Void {
        var ddata = cast(Json.parse(e.data), Array<Dynamic>);
        var newData = new Array<Dynamic>();
        for(i in ddata) {
            var actions = {
                item: i.item,
                prod: i.prod,
                name: i.name,
                quantity: i.quantity,
                price: i.price,
                actions: ["edit"]};
            Reflect.setField(i, "actions", actions);
            newData.push(i);
        }
        list.data = newData;
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