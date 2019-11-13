package client;

import priori.view.PriDisplay;
import priori.view.container.PriGroup;

class Utils {

    public static function addAllChildren(target: PriGroup, children: Array<PriDisplay>): Void {
        for(v in children)
            target.addChild(v);
    }

}