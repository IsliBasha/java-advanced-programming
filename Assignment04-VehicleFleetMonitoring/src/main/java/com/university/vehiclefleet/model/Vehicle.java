package com.university.vehiclefleet.model;

import java.time.LocalDate;

/**
 * Represents a vehicle in the fleet.
 */
public record Vehicle(
        String vehicleId,
        String registrationPlate,
        String make,
        String model,
        FuelType fuelType,
        int mileage,
        LocalDate nextServiceDate,
        boolean active
) {
    public Vehicle {
        if (mileage < 0) throw new IllegalArgumentException("Mileage cannot be negative.");
    }

    public Vehicle withMileage(int newMileage) {
        return new Vehicle(vehicleId, registrationPlate, make, model, fuelType, newMileage, nextServiceDate, active);
    }
}
