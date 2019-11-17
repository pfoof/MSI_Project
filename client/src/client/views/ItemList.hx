package client.views;

import priori.bootstrap.PriBSLabel;
import priori.bootstrap.type.PriBSContextualType;
import priori.bootstrap.PriBSFormLabel;
import client.Access.Signal;
import js.Browser;
import js.jquery.JqXHR;
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
    private var errorLabel: PriBSLabel;
    private var warningLabel: PriBSLabel;

    public function new() {
        super();
    }

    override function setup() {
        super.setup();

        errorLabel = new PriBSLabel();
        errorLabel.text = "";
        errorLabel.context = PriBSContextualType.DANGER;
        errorLabel.visible = false;
        warningLabel = new PriBSLabel();
        warningLabel.text = "";
        warningLabel.visible = false;
        warningLabel.context = PriBSContextualType.WARNING;

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
            new PriGridColumn("", "actions", ItemListActionRenderer, PriGridColumnSizeType.FIXED, 128, false)
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
        
        addChild(toolbuttons);
        
        addChild(errorLabel);
        addChild(warningLabel);

        Access.registerCallback(accessCallback);

        refresh();

        this.validate();
    }

    private var errorTimeout: Int;
    private function showError(s: String) {
        errorLabel.text = s;
        errorLabel.visible = true;
        validate();
        Browser.window.clearTimeout(errorTimeout);
        errorTimeout = Browser.window.setTimeout(function() {
            errorLabel.visible = false;
        }, 3500);
    }

    private function showWarning(s: String) {
        warningLabel.text = s;
        warningLabel.visible = true;
        validate();
    }

    private function accessCallback(signal: Signal, data: Dynamic): Void {
        switch(signal) {
            case Add | Quantity | Edit: {
                refresh();
            }

            case Delete: {
                if(
                    Reflect.hasField(data, "e")
                    && (data.e.state() == "resolved" || data.e.state() == "success")
                    && (data.e.status >= 200 && data.e.status < 300)
                ) {
                    refresh();
                    trace("Deleted!");
                } else {
                    trace("Delete has statusCode != 200");
                    var _statusCode = -1;
                    var _state = "...";
                    var _data = "";
                    if(Reflect.hasField(data, "e")) {
                        _statusCode = data.e.status;
                        _state = data.e.state();
                    }
                    if(Reflect.hasField(data, "data") && data.data != null)
                        if(data.data.length > 100)
                            _data = data.data.substr(0,100);
                        else
                            _data = data.data;
                    showError("Error ("+_statusCode+"/"+_state+"): "+_data);
                }
            }

            case Retrieve: {
                if(
                    Reflect.hasField(data, "e")
                    && (data.e.state() == "resolved" || data.e.state() == "success")
                    && (data.e.status >= 200 && data.e.status < 300)
                ) {
                    onLoad(data);
                    trace("Loaded!");
                } else {
                    trace("Retrieve has statusCode != 200");
                    var _statusCode = -1;
                    var _state = "...";
                    var _data = "";
                    if(Reflect.hasField(data, "e")) {
                        _statusCode = data.e.status;
                        _state = data.e.state();
                    }
                    if(Reflect.hasField(data, "data") && data.data != null)
                        if(data.data.length > 100)
                            _data = data.data.substr(0,100);
                        else
                            _data = data.data;
                    showError("Error ("+_statusCode+"/"+_state+"): "+_data);
                }
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
        toolbuttons.validate();

        refreshButton.x = 48;
        refreshButton.y = 48;

        list.x = 48;
        list.width = this.width - 48*2;
        list.y = 24;
        list.height = this.height - list.y - 24;

        errorLabel.centerX = this.width/2;
        errorLabel.y = 48;
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
        var lev = Utils.getUserLevel();
        toolbuttons.addButtonShow(lev);
        if(lev < 0) return;
        var ddata = cast(Json.parse(e.data), Array<Dynamic>);
        var newData = new Array<Dynamic>();
        for(i in ddata) {
            var actions = {
                item: i.item,
                prod: i.prod,
                name: i.name,
                quantity: i.quantity,
                price: i.price,
                actions: lev};
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

    override function reset(?data:Map<String, String>) {
        super.reset(data);
        warningLabel.visible = false;
        if(data != null && data.exists("warn")) {
            showWarning(data.get("warn"));
        }
    }



}