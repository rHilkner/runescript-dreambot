package nodes;

import scriptz.RunescriptAbstractContext;

public abstract class Node {
    protected final RunescriptAbstractContext ctx;

    protected Node(RunescriptAbstractContext ctx) {
        this.ctx = ctx;
    }

    public abstract boolean validate();
    public abstract int execute();
}
