package client.views;

import priori.bootstrap.type.PriBSContextualType;
import priori.net.PriURLLoader;
import priori.event.PriTapEvent;
import priori.bootstrap.PriBSImage;
import priori.view.layout.PriVerticalLayout;
import priori.types.PriFormInputTextFieldType;
import priori.bootstrap.PriBSFormInputText;
import priori.bootstrap.PriBSLabel;

class AddItemForm extends PriGroupWithState {

    public static inline final NAME = "additem";

    private var nameLabel: PriBSLabel;
    private var prodLabel: PriBSLabel;
    private var quantityLabel: PriBSLabel;
    private var priceLabel: PriBSLabel;
    private var idLabel: PriBSLabel;
    private var errorLabel: PriBSLabel;

    private var nameInput: PriBSFormInputText;
    private var prodInput: PriBSFormInputText;
    private var quantityInput: PriBSFormInputText;
    private var priceInput: PriBSFormInputText;

    private var layout: PriVerticalLayout;

    private var backButton: PriBSImage;
    private var saveButton: PriBSImage;

    override function setup() {
        super.setup();

        backButton = new PriBSImage();
        backButton.load('images/back.png');
        backButton.addEventListener(PriTapEvent.TAP,
            _ -> ContentManager.getManager()
                .switchContent(ItemList.NAME)
        );

        saveButton = new PriBSImage();
        saveButton.load('images/save.png');
        saveButton.addEventListener(PriTapEvent.TAP,
            _ -> ContentManager.getManager()
                .switchContent(ItemList.NAME)
        );

        layout = new PriVerticalLayout();
        layout.alignType = CENTER;
        layout.autoSizeContainer =
        layout.autoSizeElements = false;
        layout.gap = 16;

        nameLabel = new PriBSLabel();
        prodLabel = new PriBSLabel();
        quantityLabel = new PriBSLabel();
        priceLabel = new PriBSLabel();
        idLabel = new PriBSLabel();
        errorLabel = new PriBSLabel();

        nameLabel.text = "Name";
        prodLabel.text = "Manufacturer";
        quantityLabel.text = "Quantity:";
        priceLabel.text = "Price:";
        idLabel.text = "ID: <new item>";
        errorLabel.text = "";
        errorLabel.context = PriBSContextualType.DANGER;
        errorLabel.visible = false;

        nameInput = new PriBSFormInputText();
        prodInput = new PriBSFormInputText();
        quantityInput = new PriBSFormInputText();
        priceInput = new PriBSFormInputText();

        quantityInput.fieldType = PriFormInputTextFieldType.NUMBER;
        quantityInput.getJSElement().setAttribute("step","1");

        priceInput.fieldType = PriFormInputTextFieldType.NUMBER;
        priceInput.getJSElement().setAttribute("step", "0.01");

        addChildHere([ layout ]);

        Utils.addAllChildren(layout, [ backButton, idLabel, nameLabel, nameInput, prodLabel, prodInput, priceLabel, priceInput, quantityLabel, quantityInput, errorLabel, saveButton ]);

        this.validate();

        Access.registerCallback( (signal, data) -> {
            switch(signal) {
                case Add | Edit: {
                    var req: PriURLLoader = cast(data.data, PriURLLoader);
                    if(req.status == Constants.RESPONSE_STATUS_OK) {
                        ContentManager.getManager().switchContent(ItemList.NAME);
                    } else if(req.status == Constants.RESPONSE_STATUS_UNAUTH) {
                        errorLabel.visible = true;
                        errorLabel.text = req.data;
                    }
                    
                }
                default: {}
            }
        });
    }

    override function paint() {
        super.paint();
        
        layout.x = 24; layout.y = 24;
        layout.width = width - 48;
        layout.height = height - 48;
    }

    private var itemID = 0;

    override function reset(?data:Map<String, String>) {
        errorLabel.visible = false;
        if(
            data == null ||
            !data.exists(Constants.RESET_FORM_DATA_ID) ||
            Std.parseInt(data.get(Constants.RESET_FORM_DATA_ID)) == null ||
            Std.parseInt(data.get(Constants.RESET_FORM_DATA_ID)) <= 0
        ) {
            //New item
            idLabel.text = "ID: <new item>";
            nameInput.value = "";
            prodInput.value = "";
            priceInput.value = "1.00";
            quantityInput.value = "1";
            quantityInput.disabled = false;
            itemID = 0;
        } else {
            //Edit item
            var id = Std.parseInt(data.get(Constants.RESET_FORM_DATA_ID));
            idLabel.text = "ID: "+id;
            itemID = id;
            
            if(data.exists(Constants.RESET_FORM_DATA_NAME))
                nameInput.value = data.get(Constants.RESET_FORM_DATA_NAME);
            else nameInput.value = "";

            if(data.exists(Constants.RESET_FORM_DATA_PROD))
                prodInput.value = data.get(Constants.RESET_FORM_DATA_PROD);
            else prodInput.value = "";

            if(data.exists(Constants.RESET_FORM_DATA_QUANTITY))
                quantityInput.value = data.get(Constants.RESET_FORM_DATA_QUANTITY);
            else quantityInput.value = "1";

            quantityInput.disabled = true;

            if(data.exists(Constants.RESET_FORM_DATA_PRICE))
                priceInput.value = data.get(Constants.RESET_FORM_DATA_PRICE);
            else priceInput.value = "1.00";
        }
    }

    private function save() {
        var name = nameInput.value;
        var prod = prodInput.value;
        var price = Std.parseFloat(priceInput.value);
        var quant = Std.parseInt(quantityInput.value);

        if(itemID <= 0) {
            Access.getAccessTarget().addProduct("", name, prod, price, quant);
        } else {
            Access.getAccessTarget().editProduct("", itemID, name, prod, price);
        }
    }

}