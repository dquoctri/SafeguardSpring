package com.dqtri.mango.safeguard.audit;

public class AuditInfoHolder {
    private AuditInfoHolder(){}
    private static class SingletonHelper {
        private static final AuditInfoHolder INSTANCE = new AuditInfoHolder();
    }
    public static AuditInfoHolder getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private final ThreadLocal<AuditInfo> thread = new ThreadLocal<>();

    public AuditInfo getCurrent() {
        return thread.get();
    }

    public void setCurrentContext(AuditInfo auditTrailInfo) {
        thread.set(auditTrailInfo);
    }
}
