package org.example.tdd_lab3.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseMetadata {
    private int code = 200;
    private boolean success = true;
    private String errorMessage;
}