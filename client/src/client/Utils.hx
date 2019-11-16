package client;

import client.views.LoginForm;
import client.views.ContentManager;
import js.jquery.JQuery;
import js.Browser;
import priori.view.PriDisplay;
import priori.view.container.PriGroup;

class Utils {

    public static function addAllChildren(target: PriGroup, children: Array<PriDisplay>): Void {
        for(v in children)
            target.addChild(v);
    }

    public static function getToken(): String {
        return Browser.getLocalStorage().getItem(Constants.TOKEN_HEADER);
    }

    public static function ping(): Bool {
        if(!Constants.hasConnection()) return false;
        var jqxhr = JQuery.ajax(Constants.SERVER_DEST, {method: "GET", async:false});
        return jqxhr.state() == "resolved";
    }

    public static function verifyToken(s: String): Bool {
        return false;
    }

    public static function logout() {
        Browser.getLocalStorage().removeItem(Constants.TOKEN_HEADER);
        ContentManager.getManager().switchContent(LoginForm.NAME, ["error" => "Please log in again!"]);
    }

}