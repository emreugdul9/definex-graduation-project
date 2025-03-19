package com.definexjavaspringbootbootcamp.definexgraduationproject.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TaskAssignedResponse extends Response {

    private UUID taskId;
    private UUID userId;

}
