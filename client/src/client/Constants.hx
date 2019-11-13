package client;

import js.lib.Date;

class Constants {
    public static final LOCAL_STORAGE_LOG = "locallog";

    public static final ITEM_DELETE = 0xEFFFFFFF;

    public static inline function hasConnection(): Bool { return js.Browser.navigator.onLine; }

    public static inline function getTimestamp(): Float { return new js.lib.Date().getTime(); }

    public static final SERVER_DEST = "https://localhost:8080";

    public static final TOKEN_HEADER = "X-Auth-Token";

    public static final RESET_FORM_DATA_ID = "id";
    public static final RESET_FORM_DATA_NAME = "name";
    public static final RESET_FORM_DATA_PROD = "prod";
    public static final RESET_FORM_DATA_QUANTITY = "quantity";
    public static final RESET_FORM_DATA_PRICE = "price";

    public static final RESPONSE_STATUS_UNAUTH = 403;
    public static final RESPONSE_STATUS_OK = 200;
}