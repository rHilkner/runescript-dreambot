package shared.services;

import scriptz.RunescriptAbstractContext;

public abstract class AbstractService {

    public static RunescriptAbstractContext ctx;

    public AbstractService() {
        ctx = RunescriptAbstractContext.ctx;
    }

}
