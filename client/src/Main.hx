package ;

import client.ItemList;
import priori.style.font.PriFontStyleWeight;
import priori.style.font.PriFontStyle;
import priori.view.text.PriText;
import priori.app.PriApp;

import client.LoginForm;


class Main extends PriApp {

    private var loginForm: client.LoginForm;
    private var itemList: client.ItemList;

    public function new() {
        super();
    }

    override private function setup():Void {
        this.addChild(itemList = new ItemList());
    }

    override private function paint():Void {
        //loginForm.width = this.width;
        //loginForm.height = this.height;
        itemList.width = this.width;
        itemList.height = this.height;
    }

}
