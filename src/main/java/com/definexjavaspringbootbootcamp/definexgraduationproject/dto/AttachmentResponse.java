package com.definexjavaspringbootbootcamp.definexgraduationproject.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentResponse extends Response {
    private String filePath;

}
