package com.lvlup.tienda.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ErrorDTO {
    private String message;
    private String details;
    private LocalDateTime timestamp;
    private int status;
}
