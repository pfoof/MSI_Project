package client.views;

import priori.bootstrap.type.PriBSImageStyle;
import priori.bootstrap.PriBSImage;
import priori.view.container.PriGroup;

class Toolbuttons extends PriGroup {

    private var imageAdd: PriBSImage;

    override function setup() {
        super.setup();

        imageAdd = new PriBSImage();
        imageAdd.imageStyle = PriBSImageStyle.CIRCLE;

    }

    override function paint() {
        super.paint();

    }

}