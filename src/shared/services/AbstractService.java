package shared.services;

import shared.RunescriptAbstractContext;

public abstract class AbstractService {

    public static RunescriptAbstractContext ctx;

    public AbstractService() {
        ctx = RunescriptAbstractContext.ctx;
    }

}
