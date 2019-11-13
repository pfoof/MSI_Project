package ;

import priori.assets.AssetImage;
import priori.assets.AssetManager;
import client.views.*;
import priori.style.font.PriFontStyleWeight;
import priori.style.font.PriFontStyle;
import priori.view.text.PriText;
import priori.app.PriApp;

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

        ContentManager.getManager().switchContent(ItemList.NAME);
    }

    override private function paint():Void {
        ContentManager.getManager().width = this.width;
        ContentManager.getManager().height = this.height;
    }

}
