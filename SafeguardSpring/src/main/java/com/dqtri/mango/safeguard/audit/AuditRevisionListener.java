package com.dqtri.mango.safeguard.audit;

import com.dqtri.mango.safeguard.model.AuditRevisionEntity;
import org.hibernate.envers.RevisionListener;

public class AuditRevisionListener implements RevisionListener {
    @Override
    public void newRevision(Object revisionEntity) {
        AuditInfo audit = AuditInfoHolder.getInstance().getCurrent();
        if (revisionEntity instanceof AuditRevisionEntity auditRevisionEntity){
            auditRevisionEntity.setAction(audit.getAction());
            auditRevisionEntity.setUri(audit.getUri());
            auditRevisionEntity.setEmail(audit.getEmail());
            auditRevisionEntity.setDescription(audit.getDescription());
        }
    }
}
