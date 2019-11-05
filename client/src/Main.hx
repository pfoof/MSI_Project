package ;

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
    }

    override private function setup():Void {
        ContentManager.getManager()
            .addScene(ItemList.NAME, new ItemList())
            .addScene(LoginForm.NAME, new LoginForm())
            .addScene(RegisterForm.NAME, new RegisterForm());

        this.addChild(ContentManager.getManager());

        ContentManager.getManager().switchContent(ItemList.NAME);
    }

    override private function paint():Void {
        ContentManager.getManager().width = this.width;
        ContentManager.getManager().height = this.height;
    }

}
