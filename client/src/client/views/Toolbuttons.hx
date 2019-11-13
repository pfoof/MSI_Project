package client.views;

import priori.event.PriTapEvent;
import priori.bootstrap.type.PriBSImageStyle;
import priori.bootstrap.PriBSImage;
import priori.view.container.PriGroup;

class Toolbuttons extends PriGroup {

    private var imageAdd: PriBSImage;
    private var imageRefresh: PriBSImage;

    override function setup() {
        super.setup();

        imageAdd = new PriBSImage();
        imageAdd.imageStyle = PriBSImageStyle.CIRCLE;
        imageAdd.load("images/add.png");

        imageRefresh = new PriBSImage();
        imageRefresh.imageStyle = PriBSImageStyle.CIRCLE;
        imageRefresh.load("images/refresh.png");

        imageAdd.addEventListener(PriTapEvent.TAP,
            _ -> ContentManager
                .getManager()
                .switchContent(AddItemForm.NAME)
        );

        imageRefresh.addEventListener(PriTapEvent.TAP,
            _ -> cast(this.parent, ItemList)
                .refresh()
        );

        addChild(imageAdd);
        addChild(imageRefresh);

    }

    override function paint() {
        super.paint();

        imageAdd.y = imageRefresh.y = 0;
        imageAdd.width = imageRefresh.width =
        this.height;
        imageAdd.imageScaleY = imageAdd.imageScaleX;
        imageRefresh.imageScaleY = imageRefresh.imageScaleX;
        imageRefresh.x = this.width - imageRefresh.width - 12;
        imageAdd.x = imageRefresh.x - 12 - imageAdd.width;

    }

}