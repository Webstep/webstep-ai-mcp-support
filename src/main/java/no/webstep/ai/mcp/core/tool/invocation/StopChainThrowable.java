package no.webstep.ai.mcp.core.tool.invocation;


/**
 * This is meant as an emergency stop for tools to halt the execution plan early
 */
public final class StopChainThrowable extends RuntimeException {
    private final Object payload;
    public StopChainThrowable(Object payload) { super(null, null, false, false); this.payload = payload; }
    public Object payload() { return payload; }
}
