package client.views;

import priori.view.container.PriGroup;

class ContentManager extends PriGroup {

    private static var contentManager: ContentManager;
    private var contents: Map<String, PriGroupWithState>;
    private var current: PriGroupWithState;

    public function new() {
        super();
        contents = new Map<String, PriGroupWithState>();
    }

    override function setup() {
        super.setup();
    }

    public function addScene(name: String, scene: PriGroupWithState): ContentManager {
        contents.set(name, scene);
        return this;
    }

    public function switchContent(scene: String, ?data: Map<String, String>) {
        if(contents.exists(scene)) {
            if(current != null) {
                removeChild(current);
                current = null;
            }

            current = contents.get(scene);
            addChild(current);
            current.reset(data);

            validate();
        }
    }

    override function paint() {
        super.paint();
        if(current != null) {
            current.width = this.width;
            current.height = this.height;
            current.x = this.x;
            current.y = this.y;
        }
    }

    public static function getManager(): ContentManager {
        if(contentManager == null || contentManager.isKilled())
            contentManager = new ContentManager();
        return contentManager;
    }
}