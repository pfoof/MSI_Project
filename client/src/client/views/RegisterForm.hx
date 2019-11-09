package client.views;

import priori.bootstrap.type.PriBSContextualType;
import priori.bootstrap.PriBSLabel;
import priori.view.component.PriExtendable;
import priori.style.border.PriBorderStyle;
import priori.geom.PriColor;
import priori.bootstrap.PriBSFormButton;
import priori.bootstrap.PriBSFormInputText;
import priori.bootstrap.PriBSFormLabel;
import priori.view.container.PriGroup;
import priori.event.*;
import priori.style.border.PriBorderType;
import js.Browser;

class RegisterForm extends PriGroupWithState {

    public static inline final NAME = "register";

    var margin = 48;
    var spacing = 32;
    var inSpacing = 24;

    private var usernameLabel: PriBSFormLabel;
    private var usernameInput: PriBSFormInputText;
    private var passwordLabel: PriBSFormLabel;
    private var passwordInput: PriBSFormInputText;
    private var repeatLabel: PriBSFormLabel;
    private var repeatInput: PriBSFormInputText;
    private var emailLabel: PriBSFormLabel;
    private var emailInput: PriBSFormInputText;
    private var registerButton: PriBSFormButton;

    public function new() {
        super();
    }

    override private function setup(): Void {
        super.setup();

        usernameLabel = new PriBSFormLabel();
        usernameLabel.text = "Username";
        usernameInput = new PriBSFormInputText();
        usernameInput.placeholder = "Username";

        passwordLabel = new PriBSFormLabel();
        passwordLabel.text = "Password";
        passwordInput = new PriBSFormInputText();
        passwordInput.password = true;
        passwordInput.placeholder = "Password";
        passwordInput.addEventListener(PriEvent.CHANGE, passwordChanged);

        repeatLabel = new PriBSFormLabel();
        repeatLabel.text = "Repeat password";
        repeatInput = new PriBSFormInputText();
        repeatInput.password = true;
        repeatInput.placeholder = "Repeat";
        repeatInput.addEventListener(PriEvent.CHANGE, passwordChanged);

        emailLabel = new PriBSFormLabel();
        emailLabel.text = "E-mail";
        emailInput = new PriBSFormInputText();
        emailInput.placeholder = "E-mail";

        registerButton = new PriBSFormButton();
        registerButton.text = "Register";

        addChild(usernameLabel);
        addChild(usernameInput);
        addChild(passwordLabel);
        addChild(passwordInput);
        addChild(repeatLabel);
        addChild(repeatInput);
        addChild(emailLabel);
        addChild(emailInput);
        addChild(registerButton);

    }

    override function paint() {
        super.paint();

        passwordInput.x = passwordLabel.x =
        usernameInput.x = usernameLabel.x =
        repeatLabel.x = repeatInput.x =
        emailInput.x = emailLabel.x =
            margin;

        passwordInput.width = usernameInput.width =
        repeatInput.width = emailInput.width =
            width - margin * 2 - spacing * 2;
        
        usernameLabel.width = width/2 - 2*margin;
        usernameLabel.y = margin;
        usernameInput.y = usernameLabel.maxY + inSpacing;

        passwordLabel.y = usernameInput.maxY + spacing;
        passwordInput.y = passwordLabel.maxY + inSpacing;

        repeatLabel.y = passwordInput.maxY + spacing;
        repeatInput.y = repeatLabel.maxY + inSpacing;

        emailLabel.y = repeatInput.maxY + spacing;
        emailInput.y = emailLabel.maxY + inSpacing;

        registerButton.y = emailInput.maxY + spacing;
        registerButton.centerX = width/2;

        updateErrorLabelsPaint();
    }

    private var errorLabels = new Map<PriExtendable, PriBSLabel>();

    private function updateErrorLabelsPaint() {
        for (key => value in errorLabels) {
            value.centerY = key.centerY;
            value.x = margin;
            key.x = value.maxX + spacing;
        }
    }

    private function setError(p: PriExtendable, text: String) {
        var e = new PriBSLabel();
        e.x = margin;
        e.text = text;
        e.context = PriBSContextualType.DANGER;
        e.centerY = p.centerY;
        p.x = e.maxX + spacing;
        addChild(e);
        errorLabels.set(p, e);
    }

    private function unsetError(p: PriExtendable) {
        p.x = margin;
        if( errorLabels.exists(p) ) {
            var e = errorLabels.get(p);
            if(e != null) {
                removeChild(e);
                e.visible = false;
            }
            errorLabels.remove(p);
        }
    }

    private function passwordChanged(e: Dynamic): Void {
        if(passwordInput.value != repeatInput.value) {
            var border = new PriBorderStyle();
            border
                .setColor(PriColor.fromString("#EE1111"))
                .setType(PriBorderType.SOLID)
                .setWidth(2.0);
            repeatInput.border = passwordInput.border = border;
            setError(repeatLabel, "Password do not match!");
        } else {
            passwordInput.border = repeatInput.border = null;
            unsetError(repeatLabel);
        }
    }

}