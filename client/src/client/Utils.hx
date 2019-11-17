package client;

import js.html.XMLHttpRequestResponseType;
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

    public static function getUserLevel(): Int {
        var lev = Browser.getLocalStorage().getItem("level");
        //if no level is saved
        if(lev == null || lev == "" || lev == "undefined") {
            //Check connection -> if no then zero level
            if(!Utils.ping()) return 0;
            //If no token is available -> zero level
            var tok = Utils.getToken();
            if(tok == null || tok == "" || tok == "undefined") return 0;
            //Get new level by token
            var _headers = {};
            Reflect.setField(_headers, Constants.TOKEN_HEADER, tok);
            var xhr = JQuery.ajax(Constants.SERVER_DEST+"/authorize", {
                headers: _headers,
                method: "GET",
                async: false
            });

            //If token is invalid -> logout and ret zero level
            if(xhr.status < 100 || xhr.status >= 400) {
                Utils.logout();
                return 0;
            } else {
                //Else save level to browser
                if(xhr.responseType == XMLHttpRequestResponseType.JSON && Reflect.hasField(xhr.response, "level")) {
                    lev = xhr.response.level;
                    Browser.getLocalStorage().setItem("level", lev);
                }
            }
            return Std.parseInt(lev);
        } else return Std.parseInt(lev);
    }

}