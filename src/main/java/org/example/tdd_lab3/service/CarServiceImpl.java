package org.example.tdd_lab3.service;

import lombok.RequiredArgsConstructor;
import org.example.tdd_lab3.model.Car;
import org.example.tdd_lab3.model.PaginationMetaData;
import org.example.tdd_lab3.repo.CarRepository;
import org.example.tdd_lab3.request.CarPageRequest;
import org.example.tdd_lab3.response.ApiResponse;
import org.example.tdd_lab3.response.BaseMetadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    @Override
    public Car getById(String id) {
        return carRepository.findById(id).orElse(null);
    }

    @Override
    public ApiResponse<BaseMetadata, Car> getByIdAsApiResponse(String id) {
        Car car = getById(id);

        if (car != null) {
            BaseMetadata meta = new BaseMetadata(200, true);
            return new ApiResponse<>(meta, car);
        } else {
            BaseMetadata meta = BaseMetadata.builder()
                    .code(404)
                    .success(false)
                    .errorMessage("Not found")
                    .build();
            return new ApiResponse<>(meta);
        }
    }

    @Override
    public ApiResponse<BaseMetadata, Car> getByBrandAsApiResponse(String brand) {
        List<Car> cars = carRepository.findByBrand(brand);

        if (cars.isEmpty()) {
            return new ApiResponse<>(BaseMetadata.builder()
                    .code(404)
                    .success(false)
                    .errorMessage("No cars found for brand: " + brand)
                    .build());
        }

        return ApiResponse.<BaseMetadata, Car>builder()
                .meta(new BaseMetadata(200, true))
                .data(cars)
                .build();
    }

    @Override
    public ApiResponse<BaseMetadata, Car> getAllAsApiResponse() {
        List<Car> cars = carRepository.findAll();
        BaseMetadata meta = BaseMetadata.builder()
                .code(cars.isEmpty() ? 404 : 200)
                .success(!cars.isEmpty())
                .errorMessage(cars.isEmpty() ? "List is empty" : null)
                .build();

        return ApiResponse.<BaseMetadata, Car>builder()
                .meta(meta)
                .data(cars)
                .build();
    }

    @Override
    public ApiResponse<BaseMetadata, Car> createAsApiResponse(Car car) {
        Car saved = carRepository.save(car);
        return new ApiResponse<>(new BaseMetadata(200, true), saved);
    }

    @Override
    public ApiResponse<BaseMetadata, Car> updateAsApiResponse(String id, Car car) {
        if (!carRepository.existsById(id)) {
            return new ApiResponse<>(BaseMetadata.builder()
                    .code(404)
                    .success(false)
                    .errorMessage("Not found")
                    .build());
        }
        car.setId(id);
        Car updated = carRepository.save(car);
        return new ApiResponse<>(new BaseMetadata(200, true), updated);
    }

    @Override
    public ApiResponse<BaseMetadata, Car> deleteAsApiResponse(String id) {
        if (carRepository.existsById(id)) {
            carRepository.deleteById(id);
            return new ApiResponse<>(new BaseMetadata(200, true));
        }
        return new ApiResponse<>(BaseMetadata.builder()
                .code(404)
                .success(false)
                .errorMessage("Not found")
                .build());
    }



    /// //lab Panging /////////
    @Override
    public ApiResponse<PaginationMetaData, Car> getCarsPage(CarPageRequest request) {
        Pageable pageable = PageRequest.of(request.page(), request.size(), Sort.by(Sort.Direction.DESC, "id"));        Page<Car> page = carRepository.findAll(pageable);

        PaginationMetaData metaData = PaginationMetaData.builder()
                .code(200)
                .success(true)
                .number(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .build();

        if (page.getTotalElements() == 0) {
            metaData.setSuccess(false);
            metaData.setCode(404);
            metaData.setErrorMessage("List is empty");
            return new ApiResponse<>(metaData, page.getContent());
        }

        if (request.page() >= page.getTotalPages()) {
            metaData.setSuccess(false);
            metaData.setCode(400);
            metaData.setErrorMessage("Page out of range");
            return new ApiResponse<>(metaData, page.getContent());
        }

        return new ApiResponse<>(metaData, page.getContent());
    }

}