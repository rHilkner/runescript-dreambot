package shared.services;

import shared.RunescriptAbstractContext;

public abstract class AbstractService {

    public static RunescriptAbstractContext ctx;
    public XptZenAntibanService antibanService;

    public AbstractService() {
        ctx = RunescriptAbstractContext.ctx;
        this.antibanService = XptZenAntibanService.getInstance();
    }

}
