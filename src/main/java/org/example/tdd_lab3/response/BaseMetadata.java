package org.example.tdd_lab3.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class BaseMetadata {
    @Builder.Default
    private int code = 200;
    @Builder.Default
    private boolean success = true;
    @Builder.Default
    private String errorMessage = null;

    public BaseMetadata(int code, boolean success) {
        this.code = code;
        this.success = success;
    }
}