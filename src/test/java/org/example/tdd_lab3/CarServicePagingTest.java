package org.example.tdd_lab3;

import org.example.tdd_lab3.model.Car;
import org.example.tdd_lab3.model.PaginationMetaData;
import org.example.tdd_lab3.repo.CarRepository;
import org.example.tdd_lab3.request.CarPageRequest;
import org.example.tdd_lab3.response.ApiResponse;
import org.example.tdd_lab3.service.CarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CarServicePagingTest {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarService underTest;

    @BeforeEach
    void setUp() {
        carRepository.deleteAll();

        for (int i = 1; i <= 30; i++) {
            carRepository.save(Car.builder()
                    .brand("Brand_" + i)
                    .model("Model_" + i)
                    .description("Description of car " + i)
                    .build());
        }
    }

    @Test
    void whenHappyPathThenOk() {
        // Given
        CarPageRequest request = new CarPageRequest(0, 5);

        // When
        ApiResponse<PaginationMetaData, Car> response = underTest.getCarsPage(request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getMeta());

        assertEquals(200, response.getMeta().getCode());
        assertTrue(response.getMeta().isSuccess());
        assertNull(response.getMeta().getErrorMessage());

        assertEquals(0, response.getMeta().getNumber());
        assertEquals(5, response.getMeta().getSize());
        assertEquals(30, response.getMeta().getTotalElements());
        assertEquals(6, response.getMeta().getTotalPages()); // 30 / 5 = 6 сторінок
        assertTrue(response.getMeta().isFirst());
        assertFalse(response.getMeta().isLast());

        assertNotNull(response.getData());
        assertFalse(response.getData().isEmpty());
        assertEquals(5, response.getData().size());
        assertEquals("Brand_30", response.getData().get(0).getBrand()); // останній доданий

    }

    @Test
    void whenSizeIs_7_AndPageIs_4_ThenIsLast_TrueAndSizeEquals_2() {
        // Математика: 30 айтемів по 7 на сторінці
        // Page 0: 7, Page 1: 7, Page 2: 7, Page 3: 7 (Разом 28)
        // Page 4: залишиться 2 (Разом 30)

        // Given
        CarPageRequest request = new CarPageRequest(4, 7);

        // When
        ApiResponse<PaginationMetaData, Car> response = underTest.getCarsPage(request);

        // Then
        assertEquals(200, response.getMeta().getCode());
        assertTrue(response.getMeta().isLast());
        assertFalse(response.getMeta().isFirst());
        assertEquals(2, response.getData().size());
        assertEquals(5, response.getMeta().getTotalPages());
    }

    @Test
    void whenTheListIsEmptyThenErrorMessageHasTheWarning() {
        // Given
        carRepository.deleteAll(); // Робимо базу порожньою
        CarPageRequest request = new CarPageRequest(0, 5);

        // When
        ApiResponse<PaginationMetaData, Car> response = underTest.getCarsPage(request);

        // Then
        assertEquals(404, response.getMeta().getCode());
        assertFalse(response.getMeta().isSuccess());
        assertEquals("List is empty", response.getMeta().getErrorMessage());
    }

    @Test
    void whenTheListIsEmptyThenMetadataAndDataAreNotNull() {
        // Given
        carRepository.deleteAll();
        CarPageRequest request = new CarPageRequest(0, 5);

        // When
        ApiResponse<PaginationMetaData, Car> response = underTest.getCarsPage(request);

        // Then
        assertNotNull(response.getMeta());
        assertNotNull(response.getData());
        assertTrue(response.getData().isEmpty());
    }

    @Test
    void whenPageValueIsOutOfRangeThenErrorMessageHasTheWarning() {
        // Given: Всього 6 сторінок (0-5), просимо 10-ту
        CarPageRequest request = new CarPageRequest(10, 5);

        // When
        ApiResponse<PaginationMetaData, Car> response = underTest.getCarsPage(request);

        // Then
        assertEquals(400, response.getMeta().getCode());
        assertFalse(response.getMeta().isSuccess());
        assertEquals("Page out of range", response.getMeta().getErrorMessage());
        assertTrue(response.getData().isEmpty());
    }

    @Test
    void whenSizeGreaterThanTotal_thenFirstAndLastAreTrue() {
        // Given: просимо 50 машин, а в базі лише 30
        CarPageRequest request = new CarPageRequest(0, 50);

        // When
        ApiResponse<PaginationMetaData, Car> response = underTest.getCarsPage(request);

        // Then
        assertEquals(200, response.getMeta().getCode());
        assertEquals(30, response.getMeta().getTotalElements());
        assertEquals(1, response.getMeta().getTotalPages());

        assertTrue(response.getMeta().isFirst());
        assertTrue(response.getMeta().isLast());
        assertEquals(30, response.getData().size()); // Маємо отримати всі 30 машин
    }

    @Test
    void whenMiddlePage_thenFirstAndLastAreFalse() {
        // Given: 30 машин, по 10 на сторінці. Просимо 1-шу сторінку (це середня, бо є ще 0 та 2)
        CarPageRequest request = new CarPageRequest(1, 10);

        // When
        ApiResponse<PaginationMetaData, Car> response = underTest.getCarsPage(request);

        // Then
        assertEquals(1, response.getMeta().getNumber());
        assertEquals(3, response.getMeta().getTotalPages()); // 30 / 10 = 3 сторінки

        assertFalse(response.getMeta().isFirst()); // не перша
        assertFalse(response.getMeta().isLast());  // не остання
        assertEquals(10, response.getData().size());
    }

    @Test
    void whenSizeIsOne_thenTotalPagesEqualsTotalElements() {
        // Given
        CarPageRequest request = new CarPageRequest(0, 1);

        // When
        ApiResponse<PaginationMetaData, Car> response = underTest.getCarsPage(request);

        // Then
        assertEquals(30, response.getMeta().getTotalElements());
        assertEquals(30, response.getMeta().getTotalPages());
        assertEquals(1, response.getData().size());
        assertTrue(response.getMeta().isFirst());
        assertFalse(response.getMeta().isLast());
    }
}