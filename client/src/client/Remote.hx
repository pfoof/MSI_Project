package client;

import priori.net.PriURLLoader;
import priori.net.PriURLHeader;
import priori.net.PriURLRequest;
import haxe.Json;


class Remote extends Access {

    private static var broadcasts: Array< Dynamic->Void > = new Array< Dynamic->Void >();

    public static function addBroacast(callback: Dynamic->Void) {
        
    }

    private static function triggerBroadcasts(object: Dynamic) {
        for(b in broadcasts)
            b(object);
    }


    private static function request(data: Dynamic, method: String, ?headers: Map<String, String>, ?callback: Dynamic->Void) {
        var request: PriURLRequest = new PriURLRequest(Constants.SERVER_DEST);
        request.method = method;
        request.data = data;
        if(headers != null)
            for( k=>v in headers)
                request.requestHeader.push(new priori.net.PriURLHeader(k,v));
        var requester = new PriURLLoader();
        if(callback != null)
            requester.addEventListener(priori.event.PriEvent.COMPLETE, callback);
        requester.load(request);
    }

    override public static function addProduct(token: String, name: String, manufact: String, price: Float, quant: Int, ?time: Float) {
        var data = Json.stringify({
            name: name,
            prod: manufact,
            price: price,
            quantity: quant
        });
        var headers = [Constants.TOKEN_HEADER => token];
        Remote.request(data, "POST", headers, triggerBroadcasts);
    }

    override public static function deleteProduct(token: String, item: Int, ?time: Float) {
        var data = Json.stringify({
            item: item
        });
        var headers = [Constants.TOKEN_HEADER => token];
        Remote.request(data, "DELETE", headers, triggerBroadcasts);
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

    public static function itemInsert() {
        
    }

}