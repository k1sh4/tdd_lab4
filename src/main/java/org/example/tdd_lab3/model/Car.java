package org.example.tdd_lab3.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "cars")
public class Car extends AuditMetadata {

    @Id
    private String id;
    private String brand;
    private String model;
    private String description;

    public Car(String brand, String model, String description) {
        this.brand = brand;
        this.model = model;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return getId() != null && getId().equals(car.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
