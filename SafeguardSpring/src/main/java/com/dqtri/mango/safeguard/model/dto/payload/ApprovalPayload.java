package com.dqtri.mango.safeguard.model.dto.payload;

import com.dqtri.mango.safeguard.model.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
public class ApprovalPayload {
    @NotNull
    Action action;

    @Length(min = 0, max = 300)
    String comment;

    public Status getStatus(){
        if (Action.REJECT.equals(action)) return Status.REJECTED;
        return Status.APPROVED;
    }
}
