package org.example.tdd_lab3.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.tdd_lab3.response.BaseMetadata;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PaginationMetaData extends BaseMetadata {
    private int number;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean isFirst;
    private boolean isLast;
}