package com.definexjavaspringbootbootcamp.definexgraduationproject.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttachmentDto {
    private String filePath;
}
