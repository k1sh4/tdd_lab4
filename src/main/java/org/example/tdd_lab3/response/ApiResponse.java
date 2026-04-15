package org.example.tdd_lab3.response;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ApiResponse<M extends BaseMetadata, D> {
    private M meta;
    private List<D> data;

    public ApiResponse(M meta, D data) {
        this.meta = meta;
        this.data = new ArrayList<>();
        this.data.add(data);
    }

    public ApiResponse(M meta) {
        this.meta = meta;
        this.data = new ArrayList<>();
    }
}