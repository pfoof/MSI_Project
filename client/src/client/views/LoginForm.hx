package client.views;

import js.Browser;
import priori.event.PriTapEvent;
import priori.view.layout.PriVerticalLayout;
import priori.view.container.PriGroup;
import priori.bootstrap.*;

class LoginForm extends PriGroupWithState {
    
    public static inline final NAME = "login";

    private var title: PriBSFormLabel;
    private var usernameLabel: PriBSFormLabel;
    private var passwordLabel: PriBSFormLabel;
    private var usernameInput: PriBSFormInputText;
    private var passwordInput: PriBSFormInputText;
    private var loginButton: PriBSFormButton;

    private var githubButton: PriBSFormButton;
    private var localButton: PriBSFormButton;
    private var layout: PriVerticalLayout;
    private var error: PriBSLabel;
    
    public function new() {
        super();
    }

    override function setup() {
        super.setup();

        error = new PriBSLabel();
        error.visible = false;
        error.text = "";
        error.context = DANGER;

        title = new PriBSFormLabel();
        title.text = "Login options";

        layout = new PriVerticalLayout();
        layout.autoSizeContainer = false;
        layout.autoSizeElements = false;

        githubButton = new PriBSFormButton();
        githubButton.text = "Login with GitHub";
        localButton = new PriBSFormButton();
        localButton.text = "Login locally";
        
        layout.addChild(error);
        layout.addChild(title);

        layout.addChild(githubButton);
        layout.addChild(localButton);

        addChild(layout);

        githubButton.addEventListener(PriTapEvent.TAP, _ -> Browser.window.location.replace("https://myhost:8000/login/github"));
        localButton.addEventListener(PriTapEvent.TAP, _ -> Browser.window.location.replace("https://myhost:8000/login/local"));

        this.usernameLabel = new PriBSFormLabel();
        this.usernameLabel.text = "Username";
        this.passwordLabel = new PriBSFormLabel();
        this.passwordLabel.text = "Password";
        this.usernameInput = new PriBSFormInputText();
        this.passwordInput = new PriBSFormInputText();
        this.passwordInput.password = true;

        /*this.addChild(usernameLabel);
        this.addChild(usernameInput);
        this.addChild(passwordLabel);
        this.addChild(passwordInput);*/

        validate();
    }

    override function paint() {
        var margin: Int = 20;
        super.paint();

        layout.x = layout.y = margin;
        layout.width = width - margin * 2;
        layout.height = height - margin * 2;

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

    override function reset(?data:Map<String, String>) {
        super.reset(data);
        error.visible = false;
        if(data.exists("error")) {
            error.visible = true;
            error.text = data.get("error");
        }
    }

}