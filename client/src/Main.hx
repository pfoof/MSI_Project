package ;

import js.html.XMLHttpRequestResponseType;
import js.jquery.JQuery;
import js.Browser;
import priori.assets.AssetImage;
import priori.assets.AssetManager;
import client.views.*;
import priori.style.font.PriFontStyleWeight;
import priori.style.font.PriFontStyle;
import priori.view.text.PriText;
import priori.app.PriApp;
import js.html.Storage;
import js.html.Window;

import client.*;


class Main extends PriApp {    

    public function new() {
        super(); 

        /*AssetManager.g().addToQueue(new AssetImage("button_add", "images/add.png"));
        AssetManager.g().load();*/
    }

    override private function setup():Void {
        ContentManager.getManager()
            .addScene(ItemList.NAME, new ItemList())
            .addScene(LoginForm.NAME, new LoginForm())
            .addScene(RegisterForm.NAME, new RegisterForm())
            .addScene(AddItemForm.NAME, new AddItemForm());

        this.addChild(ContentManager.getManager());

        if(Browser.location.hash != null && Browser.location.hash != "") {
            if(Browser.location.hash.indexOf("token=") >= 0) {
                var newtoken = Browser.location.hash.substring(Browser.location.hash.indexOf("token=")+"token=".length);
                if(newtoken.length > 10)
                    Browser.getLocalStorage().setItem(Constants.TOKEN_HEADER, newtoken);
            }
        }

        var token = Utils.getToken();
        if(token == null || token.length <= 1 || token == "undefined" || token == null || token == "null") {
            ContentManager.getManager().switchContent(LoginForm.NAME);
        } else {
            if(Utils.ping()) {
                var _headers = {};
                Reflect.setField(_headers, Constants.TOKEN_HEADER, token);
                var xhr = JQuery.ajax(Constants.SERVER_DEST+"/authorize", {
                    headers: _headers,
                    method: "GET",
                    async: false
                });
                if(xhr.status < 100 || xhr.status >= 400) {
                    Utils.logout();
                } else {
                    if(xhr.responseType == XMLHttpRequestResponseType.JSON && Reflect.hasField(xhr.response, "level"))
                        Browser.getLocalStorage().setItem("level", xhr.response.level);
                    ContentManager.getManager().switchContent(ItemList.NAME);
                }
            } else
                ContentManager.getManager().switchContent(ItemList.NAME, ["warn" => "Offline mode!"]);
        }
    }

    override private function paint():Void {
        ContentManager.getManager().width = this.width;
        ContentManager.getManager().height = this.height;
    }

}

@:native("window.plugins.intent") extern class CordovaPluginsIntent {
    public static function getCordovaIntent(s:Dynamic->Void, e:Void->Void):Void;
    public static function setNewIntentHandler(s:Dynamic -> Void): Void;
}
