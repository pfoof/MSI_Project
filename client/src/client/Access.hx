package client;

class Access {
    public static function addProduct(token: String, name: String, manufact: String, price: Float, quant: Int, ?time: Float) {}
    public static function deleteProduct(token: String, item: Int, ?time: Float) {}
    public static function editProduct(token: String, item: Int, name: String, manufact: String, price: Float, ?time: Float) {}
    public static function addRemoveQuantity(token: String, item: Int, delta: Int, ?time: Float) {}

    public static function getAccessTarget(): Class<Access> {
        if(Constants.hasConnection())
            return Remote;
        else return Local;
    }
}