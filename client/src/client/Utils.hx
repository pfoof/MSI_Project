package client;

import js.Browser;
import priori.view.PriDisplay;
import priori.view.container.PriGroup;

class Utils {

    public static function addAllChildren(target: PriGroup, children: Array<PriDisplay>): Void {
        for(v in children)
            target.addChild(v);
    }

    public static function getToken(): String {
        return Browser.getLocalStorage().getItem(Constants.TOKEN_HEADER);
    }

}