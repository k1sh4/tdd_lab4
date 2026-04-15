package org.example.tdd_lab3;


import org.example.tdd_lab3.model.Car;
import org.example.tdd_lab3.response.ApiResponse;
import org.example.tdd_lab3.response.BaseMetadata;
import org.example.tdd_lab3.repo.CarRepository;
import org.example.tdd_lab3.service.CarService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CarServiceTest {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarService underTest;



    @Test
    @DisplayName("11. Пошук: Знайти всі машини певного бренду")
    void whenSearchByBrand_thenReturnFilteredList() {
        // Given
        carRepository.save(new Car("BMW", "X5", "Black"));
        carRepository.save(new Car("BMW", "M3", "Blue"));
        carRepository.save(new Car("Audi", "A6", "Silver"));

        // When
        ApiResponse<BaseMetadata, Car> response = underTest.getByBrandAsApiResponse("BMW");

        // Then
        assertEquals(200, response.getMeta().getCode());
        assertEquals(2, response.getData().size(), "Мало знайти 2 машини бренду BMW");
    }

    // --- ТЕСТИ GET BY ID ---

    @Test
    @DisplayName("1. Пошук за ID: Повернення OK ApiResponse")
    void whenCarExistsThenReturnAsOkApiResponse() {
        Car carToSave = new Car("Chevrolet", "Express", "Original description");
        Car savedCar = carRepository.save(carToSave);
        String id = savedCar.getId();

        // WHEN
        Car item = underTest.getById(id);
        ApiResponse<BaseMetadata, Car> response = underTest.getByIdAsApiResponse(id);

        // THEN
        assertNotNull(response);
        assertFalse(response.getData().isEmpty());
        assertEquals(id, response.getData().get(0).getId());
        assertEquals(item, response.getData().get(0));
    }

    @Test
    @DisplayName("2. Пошук за ID: Повернення 404 (Not found)")
    void whenCarNotExistsThenReturn404ApiResponse() {
        // Given
        String id = "non_existent_id_123";

        // When
        ApiResponse<BaseMetadata, Car> response = underTest.getByIdAsApiResponse(id);

        // Then
        assertNotNull(response);
        assertTrue(response.getData().isEmpty());
        assertFalse(response.getMeta().isSuccess());
        assertEquals(404, response.getMeta().getCode());
        assertEquals("Not found", response.getMeta().getErrorMessage());
    }

    // --- ТЕСТИ GET ALL ---

    @Test
    @DisplayName("3. Отримати всі: Список заповнений")
    void whenCarsInDb_getAll_returnsList() {
        // GIVEN: Гарантуємо, що в базі є хоча б один запис спеціально для цього тесту
        carRepository.save(new Car("TestBrand", "TestModel", "TestDesc"));

        // WHEN
        ApiResponse<BaseMetadata, Car> response = underTest.getAllAsApiResponse();

        // THEN
        assertTrue(response.getMeta().isSuccess());
        assertFalse(response.getData().isEmpty(), "Список не має бути порожнім!");
        assertEquals(200, response.getMeta().getCode());
    }

    @Test
    @DisplayName("4. Отримати всі: Список порожній (404)")
    void whenDbIsEmpty_getAll_returnsError() {
        // Тимчасово очищаємо
        carRepository.deleteAll();

        // When
        ApiResponse<BaseMetadata, Car> response = underTest.getAllAsApiResponse();

        // Then
        assertEquals(404, response.getMeta().getCode());
        assertFalse(response.getMeta().isSuccess());
        assertEquals("List is empty", response.getMeta().getErrorMessage());
    }


    // --- ТЕСТИ CREATE ---

    @Test
    @DisplayName("5. Створення: Успішне збереження")
    void whenCreateCar_persistsInDb() {
        // Given
        Car newCar = new Car("Audi", "RS7", "Fast and black");

        // When
        ApiResponse<BaseMetadata, Car> response = underTest.createAsApiResponse(newCar);
        Car savedItem = response.getData().get(0);

        // Then
        assertNotNull(savedItem.getId());
        assertTrue(response.getMeta().isSuccess());
        assertTrue(carRepository.existsById(savedItem.getId()));
    }


    // --- ТЕСТИ UPDATE ---

    @Test
    @DisplayName("6. Оновлення: Успішна зміна даних")
    void whenUpdateCar_changesReflected() {
        // Given
        Car car = carRepository.save(new Car("Ford", "Focus", "Old description"));
        car.setDescription("Brand new description");

        // When
        ApiResponse<BaseMetadata, Car> response = underTest.updateAsApiResponse(car.getId(), car);

        // Then
        assertEquals(200, response.getMeta().getCode());
        assertEquals("Brand new description", response.getData().get(0).getDescription());
    }

    @Test
    @DisplayName("7. Оновлення: Об'єкта не існує (404)")
    void whenUpdateNonExistentCar_returns404() {
        // Given
        Car car = new Car("Fake", "Car", "No desc");

        // When
        ApiResponse<BaseMetadata, Car> response = underTest.updateAsApiResponse("invalid_id", car);

        // Then
        assertEquals(404, response.getMeta().getCode());
        assertEquals("Not found", response.getMeta().getErrorMessage());
    }

    // --- ТЕСТИ DELETE ---

    @Test
    @DisplayName("8. Видалення: Успішне видалення")
    void whenDeleteCar_removedFromDb() {
        // Given
        Car car = carRepository.save(new Car("Mazda", "RX8", "Rotary engine"));
        String id = car.getId();

        // When
        ApiResponse<BaseMetadata, Car> response = underTest.deleteAsApiResponse(id);

        // Then
        assertTrue(response.getMeta().isSuccess());
        assertFalse(carRepository.existsById(id));
    }


}