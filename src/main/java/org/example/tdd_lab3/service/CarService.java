package org.example.tdd_lab3.service;

import org.example.tdd_lab3.model.Car;
import org.example.tdd_lab3.model.PaginationMetaData;
import org.example.tdd_lab3.request.CarPageRequest;
import org.example.tdd_lab3.response.ApiResponse;
import org.example.tdd_lab3.response.BaseMetadata;

public interface CarService {
    Car getById(String id);

    ApiResponse<BaseMetadata, Car> getByIdAsApiResponse(String id);

    ApiResponse<BaseMetadata, Car> getAllAsApiResponse();

    ApiResponse<BaseMetadata, Car> createAsApiResponse(Car car);

    ApiResponse<BaseMetadata, Car> updateAsApiResponse(String id, Car car);

    ApiResponse<BaseMetadata, Car> deleteAsApiResponse(String id);

    ApiResponse<BaseMetadata, Car> getByBrandAsApiResponse(String brand);

    ApiResponse<PaginationMetaData, Car> getCarsPage(CarPageRequest request);
}