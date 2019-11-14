package client;

class Access {

    private static var remote: Remote;
    private static var local: Local;

    public function addProduct(token: String, name: String, manufact: String, price: Float, quant: Int, ?time: Float) {}
    public function deleteProduct(token: String, item: Int, ?time: Float) {}
    public function editProduct(token: String, item: Int, name: String, manufact: String, price: Float, ?time: Float) {}
    public function addRemoveQuantity(token: String, item: Int, delta: Int, ?time: Float) {}
    public function retrieveItems(token: String) {}
    public function addBroadcast(callback: (Signal, Dynamic)->Void) {}
    public function getUserActions(token: String): Array<String> {return [ Constants.ACTION_EDIT ];}

    public static function registerCallback(callback: (Signal, Dynamic)->Void) {
        getRemote().addBroadcast(callback);
        getLocal().addBroadcast(callback);
    }

    private static function getRemote():Remote {
        if(remote == null) remote = new Remote();
            return remote;
    }

    private static function getLocal():Local {
        if(local == null) local = new Local();
            return local;
    }

    public static function getAccessTarget(): Access {
        if(Constants.hasConnection())
            return Access.getRemote();
        else
            return Access.getLocal();
    }
}

enum Signal {
        Add;
        Retrieve;
        Delete;
        Edit;
        Quantity;
}