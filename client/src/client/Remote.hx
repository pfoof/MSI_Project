package client;

import js.jquery.JqXHR;
import js.jquery.JQuery;
import haxe.macro.Expr.Constant;
import priori.net.PriURLLoader;
import priori.net.PriURLHeader;
import priori.net.PriURLRequest;
import haxe.Json;
import client.Access.Signal;


class Remote extends Access {

    private var broadcasts: Array< (Signal, Dynamic) -> Void > = new Array< (Signal, Dynamic) -> Void >();

    public function new() {

    }

    override public function addBroadcast(callback: (Signal, Dynamic)->Void) {
        broadcasts.push(callback);
    }

    private function triggerBroadcasts(signal: Signal, object: Dynamic) {
        trace("Broadcasts");
        for(b in broadcasts) {
            b(signal, object);
            trace("Triggered a broadcast");
        }
    }


    private function request(data: Dynamic, method: String, ?headers: Map<String, String>, ?callback: (Signal, Dynamic)->Void, ?signal: Signal, ?customURL: String) {
        var _headers = {}
        if(headers != null) {
            for(k => v in headers) {
                Reflect.setField(_headers, k, v);
            }
        }
        
        JQuery.support.cors = true;

        if(customURL == null)
            customURL = "";

        if(!Std.is(data, String))
            data = Json.stringify(data);

        JQuery.ajax({
            url: Constants.SERVER_DEST+customURL,
            headers: _headers,
            contentType: "application/json",
            async: true,
            method: method,
            dataType: "text",
            data: data,
            success: function(_data: Dynamic, status: String, e:Dynamic) {
                if(callback != null)
                    callback(signal, {data: _data, e: e});
            },
            error: function(e: Dynamic, status: String, err:String) {
                if(callback != null)
                    callback(signal, {data: err, e: e});
            }
        });
        
        /*var request: PriURLRequest = new PriURLRequest(Constants.SERVER_DEST);
        request.method = method;
        request.data = data;
        if(headers != null)
            for( k=>v in headers)
                request.requestHeader.push(new priori.net.PriURLHeader(k,v));
        var requester = new PriURLLoader();
        if(callback != null)
            requester.addEventListener(priori.event.PriEvent.COMPLETE, d -> { callback(signal, d); } );
        requester.load(request);*/
    }

    override public function addProduct(token: String, name: String, manufact: String, price: Float, quant: Int, ?time: Float) {
        var data = Json.stringify({
            name: name,
            prod: manufact,
            price: price,
            quantity: quant
        });
        var headers = [Constants.TOKEN_HEADER => token];
        trace("Sending POST request");
        request(data, "POST", headers, triggerBroadcasts, Signal.Add);
    }

    override public function editProduct(token: String, item: Int, name: String, manufact: String, price: Float, ?time: Float) {
        var data = Json.stringify({
            item: item,
            name: name,
            prod: manufact,
            price: price
        });
        var headers = [Constants.TOKEN_HEADER => token];
        trace("Sending PUT request");
        request(data, "PUT", headers, triggerBroadcasts, Signal.Edit);
    }

    override public function deleteProduct(token: String, item: Int, ?time: Float) {
        var data = Json.stringify({
            item: item
        });
        var headers = [Constants.TOKEN_HEADER => token];
        request(data, "DELETE", headers, triggerBroadcasts, Signal.Delete, "/"+item);
    }

    override public function addRemoveQuantity(token: String, item: Int, delta: Int, ?time: Float) {
        var data = Json.stringify({
            item: item,
            change: delta
        });
        var headers = [Constants.TOKEN_HEADER => token];
        request(data, "PUT", headers, triggerBroadcasts, Signal.Quantity, "/"+item);

    }

    override public function retrieveItems(token: String) {
        var headers = [Constants.TOKEN_HEADER => token];
        request("", "GET", headers, triggerBroadcasts, Signal.Retrieve);
    }

    //Determine server time
    public static function itemChange(user: String, item: Int, change: Int) {
        var req = new PriURLRequest("https://localhost:8080");
        req.method = "POST";
        req.contentType = "application/json";
        req.requestHeader.push(new PriURLHeader("X-User-Key", user));
        req.data = {
            item: item,
            change: change
        };
        
        new PriURLLoader(req);
    }

    public function itemInsert() {
        
    }

}