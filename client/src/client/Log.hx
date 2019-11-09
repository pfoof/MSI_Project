package client;

import haxe.macro.Context;
import js.Browser;
import haxe.Int64;
import haxe.Json;

typedef Record = {
    var item: Int;
    var data: Dynamic;
    var time: Float;
    var context: ItemChange;
}

enum ItemChange {
    Add;
    Quantity;
    Edit;
    Delete;
}

class Log {

    private var records: Array<Record> = new Array<Record>();

    public function new() {

    }

    public static function getLog(): Log {
        var log = new Log();
        var storage = js.Browser.getLocalStorage();
        var currentLogJson = storage.getItem(Constants.LOCAL_STORAGE_LOG);
        if( currentLogJson.length <= 0 )
            return log;
        log.records = Json.parse(currentLogJson);

        return log;
    }

    public static function saveLog(log: Log) {
        var jsonLog = Json.stringify(log.records);
        Browser.getLocalStorage().setItem(Constants.LOCAL_STORAGE_LOG, jsonLog);
    }

    private function getQuantity(item: Int): Int {
        var q = 0;
        var sublist = records
            .filter( x -> x.item == item);
        sublist.sort( (x, y) -> cast(x.time - y.time, Int) );

        for(x in sublist) {
            if(x.context == ItemChange.Delete) {
                q = 0;
                return 0;
            } else if(x.context == ItemChange.Quantity) {
                q += x.data.change;
            }
        }

        return 0;
    }

    public function getNewItems(): Array<ItemModel> {
        var items = new Array<ItemModel>();
        var sublist = records
            .filter( x -> x.context == ItemChange.Add || x.context == ItemChange.Delete );
        sublist.sort( (x, y) -> cast(x.time - y.time, Int) );

        for(x in sublist) {
            if(x.context == ItemChange.Delete) {
                items = items.filter( y -> y.ID != x.item);
            } else if(x.context == ItemChange.Add) {
                var newItem = new ItemModel();
                newItem.ID = x.item;
                newItem.name = x.data.name;
                newItem.prod = x.data.prod;
                newItem.quantity = x.data.quantity;
                newItem.price = x.data.price;
            }
        }

        return items;
    }

    private static function findById(items: Array<ItemModel>, id: Int): Int {
        for(i in 0...items.length)
            if(items[i].ID == id)
                return i;

        return -1;
    }

    public function filterChanges(items: Array<ItemModel>): Array<ItemModel> {
        var sublist = records
            .filter( x -> x.context == ItemChange.Edit );
        sublist.sort( (x, y) -> cast(x.time - y.time, Int) );

        for(x in sublist) {
            var target = findById(items, x.item);
            if(target > -1) {
                items[target].name = x.data.name;
                items[target].prod = x.data.prod;
                items[target].price = x.data.price;
            }
        }

        return items;
    }

    public function filterItems(items: Array<ItemModel>): Array<ItemModel> {
        var sublist = records
            .filter( x -> x.context == ItemChange.Delete );
        sublist.sort( (x, y) -> cast(x.time - y.time, Int) );
        
        for(x in sublist)
            items = items.filter( y -> y.ID != x.item );

        return filterChanges(items.concat(getNewItems()));
    }

    public function getItem(item: Int, ?currentItem: ItemModel): ItemModel {
        
    }

    public function logQuantity(time: Float, item: Int, change: Int) {
        var record: Record = {
            time: time,
            item: item,
            data: {change: change},
            context: ItemChange.Quantity
        };
        records.push(record);
    }

}