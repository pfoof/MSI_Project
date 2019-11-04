package client;

import js.Browser;
import haxe.Int64;
import haxe.Json;

typedef Record = {
    var item: Int;
    var change: Int;
    var time: Float;
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

    public function getQuantity(item: Int): Int {
        var q = 0;
        var sublist = records
            .filter( x -> x.item == item);
        sublist.sort( (x, y) -> cast(x.time - y.time, Int) );

        for(x in sublist) {
            if(x.change == Constants.ITEM_DELETE) {
                q = 0;
                return 0;
            }
            q += x.change;
        }

        return 0;
    }

    public function logQuantity(time: Float, item: Int, change: Int) {
        var record: Record = {
            time: time,
            item: item,
            change: change
        };
        records.push(record);
    }

}