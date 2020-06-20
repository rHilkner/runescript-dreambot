package Shared.Services;

import Shared.RunescriptAbstractContext;

public abstract class AbstractService {

    public static RunescriptAbstractContext ctx;

    public AbstractService() {
        ctx = RunescriptAbstractContext.ctx;
    }

}
