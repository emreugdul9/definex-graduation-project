package com.definexjavaspringbootbootcamp.definexgraduationproject.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class ChangeStateResponse extends Response {

    private String reason;

    public ChangeStateResponse(String message) {
        super(message);
    }
}
