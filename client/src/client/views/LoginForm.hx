package client.views;

import priori.view.container.PriGroup;
import priori.bootstrap.*;

class LoginForm extends PriGroup {
    
    private var usernameLabel: PriBSFormLabel;
    private var passwordLabel: PriBSFormLabel;
    private var usernameInput: PriBSFormInputText;
    private var passwordInput: PriBSFormInputText;
    private var loginButton: PriBSFormButton;
    
    public function new() {
        super();
    }

    override private function setup(): Void {
        super.setup();
        this.usernameLabel = new PriBSFormLabel();
        this.usernameLabel.text = "Username";
        this.passwordLabel = new PriBSFormLabel();
        this.passwordLabel.text = "Password";
        this.usernameInput = new PriBSFormInputText();
        this.passwordInput = new PriBSFormInputText();
        this.passwordInput.password = true;

        this.addChild(usernameLabel);
        this.addChild(usernameInput);
        this.addChild(passwordLabel);
        this.addChild(passwordInput);
    }

    override function paint() {
        var margin: Int = 20;
        super.paint();

        this.usernameLabel.width = 
        this.usernameInput.width = 
        this.passwordLabel.width = 
        this.passwordInput.width =
            this.width * 0.9;

        this.usernameLabel.centerX =
        this.usernameInput.centerX = 
        this.passwordInput.centerX =
        this.passwordLabel.centerX =
            this.width/2;

        this.usernameLabel.y = margin;
        this.usernameInput.y = this.usernameLabel.maxY + margin;
        this.passwordLabel.y = this.usernameInput.maxY + margin;
        this.passwordInput.y = this.passwordLabel.maxY + margin;

    }


}