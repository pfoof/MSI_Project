package client;

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
        for(b in broadcasts)
            b(signal, object);
    }


    private function request(data: Dynamic, method: String, ?headers: Map<String, String>, ?callback: (Signal, Dynamic)->Void, ?signal: Signal) {
        var request: PriURLRequest = new PriURLRequest(Constants.SERVER_DEST);
        request.method = method;
        request.data = data;
        if(headers != null)
            for( k=>v in headers)
                request.requestHeader.push(new priori.net.PriURLHeader(k,v));
        var requester = new PriURLLoader();
        if(callback != null)
            requester.addEventListener(priori.event.PriEvent.COMPLETE, d -> { callback(signal, d); } );
        requester.load(request);
    }

    override public function addProduct(token: String, name: String, manufact: String, price: Float, quant: Int, ?time: Float) {
        var data = Json.stringify({
            name: name,
            prod: manufact,
            price: price,
            quantity: quant
        });
        var headers = [Constants.TOKEN_HEADER => token];
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
        request(data, "PUT", headers, triggerBroadcasts, Signal.Edit);
    }

    override public function deleteProduct(token: String, item: Int, ?time: Float) {
        var data = Json.stringify({
            item: item
        });
        var headers = [Constants.TOKEN_HEADER => token];
        request(data, "DELETE", headers, triggerBroadcasts, Signal.Delete);
    }

    override public function addRemoveQuantity(token: String, item: Int, delta: Int, ?time: Float) {
        var data = Json.stringify({
            item: item,
            change: delta
        });
        var headers = [Constants.TOKEN_HEADER => token];
        request(data, "PUT", headers, triggerBroadcasts, Signal.Quantity);

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