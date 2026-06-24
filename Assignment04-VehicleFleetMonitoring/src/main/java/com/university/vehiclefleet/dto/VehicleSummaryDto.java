package com.university.vehiclefleet.dto;

/** DTO carrying a slim vehicle summary for stream-map transformations. */
public record VehicleSummaryDto(String registrationPlate, String makeModel, int mileage) {}
