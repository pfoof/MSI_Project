package client.views;

import priori.view.PriDisplay;
import priori.view.container.PriGroup;

class PriGroupWithState extends PriGroup {

    public function reset(?data: Map<String, String>) {
        
    }

    public function addChildHere(?view: Array<PriDisplay>): PriGroupWithState {
        for(v in view)
            super.addChild(v);
        return this;
    }

}