package ;

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

    private var loginForm: LoginForm;
    private var itemList: ItemList;
    private var registerForm: RegisterForm;

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

        if(Browser.location.hash != null) {
            if(Browser.location.hash.indexOf("token=") >= 0) {
                var newtoken = Browser.location.hash.substring(Browser.location.hash.indexOf("token=")+"token=".length);
                if(newtoken.length > 10)
                    Browser.getLocalStorage().setItem(Constants.TOKEN_HEADER, newtoken);
            }
        }

        var token = Utils.getToken();
        if(token == null || token.length <= 1 || token == "undefined") {
            ContentManager.getManager().switchContent(LoginForm.NAME);
        } else 
            ContentManager.getManager().switchContent(ItemList.NAME);
    }

    override private function paint():Void {
        ContentManager.getManager().width = this.width;
        ContentManager.getManager().height = this.height;
    }

}
