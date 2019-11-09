package client;

class Local extends Access {

    public function new() {

    }

    override function addProduct(token:String, name:String, manufact:String, price:Float, quant:Int, ?time:Float) {
        var log = Log.getLog();
    }
}