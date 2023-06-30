package com.dqtri.mango.safeguard.model.dto.payload.enums;

import com.dqtri.mango.safeguard.model.enums.Status;

public enum ModifyStatus {
    DRAFT(Status.DRAFT),
    SUBMITTED(Status.SUBMITTED);

    private final Status status;
    ModifyStatus(Status status){
        this.status = status;
    }

    public Status toStatus(){
        return status;
    }
}
