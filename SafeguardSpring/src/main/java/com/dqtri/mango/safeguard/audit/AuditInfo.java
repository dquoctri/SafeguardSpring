package com.dqtri.mango.safeguard.audit;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuditInfo {
    private String action;
    private String uri;
    private String email;
    private String description;
}
