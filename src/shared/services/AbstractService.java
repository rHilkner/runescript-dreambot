package shared.services;

import scriptz.RunescriptAbstractContext;

import static scriptz.RunescriptAbstractContext.logScript;

public abstract class AbstractService {

    public static RunescriptAbstractContext ctx;

    public AbstractService() {
        ctx = RunescriptAbstractContext.ctx;
    }

}
