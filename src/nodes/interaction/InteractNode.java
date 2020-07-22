package nodes.interaction;

import nodes.Node;
import scriptz.RunescriptAbstractContext;

public class InteractNode extends Node {

    protected InteractNode(RunescriptAbstractContext ctx) {
        super(ctx);
    }

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public int execute() {
        return 0;
    }
}
