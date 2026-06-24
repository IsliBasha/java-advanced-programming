package com.university.vehiclefleet.service;

import com.university.vehiclefleet.dto.VehicleSummaryDto;
import com.university.vehiclefleet.model.FuelType;
import com.university.vehiclefleet.model.Vehicle;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages the vehicle fleet using List, Set, and Map collections.
 */
public class FleetService {

    private final List<Vehicle> vehicles = new ArrayList<>();
    private final Set<String> fuelTypes = new HashSet<>();
    private final Map<String, List<Vehicle>> vehiclesByFuelType = new HashMap<>();

    // ── CRUD ─────────────────────────────────────────────────────────────

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
        fuelTypes.add(vehicle.fuelType().name());
        vehiclesByFuelType
                .computeIfAbsent(vehicle.fuelType().name(), k -> new ArrayList<>())
                .add(vehicle);
    }

    public boolean removeVehicle(String vehicleId) {
        boolean removed = vehicles.removeIf(v -> v.vehicleId().equals(vehicleId));
        if (removed) rebuildDerivedCollections();
        return removed;
    }

    public boolean updateVehicle(String vehicleId, Vehicle updated) {
        for (int i = 0; i < vehicles.size(); i++) {
            if (vehicles.get(i).vehicleId().equals(vehicleId)) {
                vehicles.set(i, updated);
                rebuildDerivedCollections();
                return true;
            }
        }
        return false;
    }

    // ── QUERY ─────────────────────────────────────────────────────────────

    public Optional<Vehicle> findById(String vehicleId) {
        return vehicles.stream()
                .filter(v -> v.vehicleId().equals(vehicleId))
                .findFirst();
    }

    /** Vehicles with mileage above threshold. */
    public List<Vehicle> findByMileageAbove(int minMileage) {
        return vehicles.stream()
                .filter(v -> v.mileage() > minMileage)
                .collect(Collectors.toList());
    }

    /** Vehicles with mileage below threshold. */
    public List<Vehicle> findByMileageBelow(int maxMileage) {
        return vehicles.stream()
                .filter(v -> v.mileage() < maxMileage)
                .collect(Collectors.toList());
    }

    /** Stream map: transforms vehicles to lightweight summary DTOs. */
    public List<VehicleSummaryDto> toSummaries() {
        return vehicles.stream()
                .map(v -> new VehicleSummaryDto(
                        v.registrationPlate(),
                        v.make() + " " + v.model(),
                        v.mileage()))
                .collect(Collectors.toList());
    }

    /** Case-insensitive partial match on make+model. */
    public List<Vehicle> searchByModelPattern(String pattern) {
        String lower = pattern.toLowerCase();
        return vehicles.stream()
                .filter(v -> (v.make() + " " + v.model()).toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    /**
     * Vehicles whose next service date is before the given threshold date
     * — i.e., overdue or soon-due vehicles.
     */
    public List<Vehicle> findServiceDueBefore(LocalDate threshold) {
        return vehicles.stream()
                .filter(v -> v.nextServiceDate().isBefore(threshold))
                .collect(Collectors.toList());
    }

    /** Filter by fuel type. */
    public List<Vehicle> findByFuelType(FuelType fuelType) {
        return vehicles.stream()
                .filter(v -> v.fuelType() == fuelType)
                .collect(Collectors.toList());
    }

    // ── ACCESSORS ─────────────────────────────────────────────────────────

    public List<Vehicle> getAllVehicles() {
        return Collections.unmodifiableList(vehicles);
    }

    public Set<String> getFuelTypes() {
        return Collections.unmodifiableSet(fuelTypes);
    }

    public Map<String, List<Vehicle>> getVehiclesByFuelType() {
        return Collections.unmodifiableMap(vehiclesByFuelType);
    }

    // ── PRIVATE HELPERS ───────────────────────────────────────────────────

    private void rebuildDerivedCollections() {
        fuelTypes.clear();
        vehiclesByFuelType.clear();
        for (Vehicle v : vehicles) {
            fuelTypes.add(v.fuelType().name());
            vehiclesByFuelType.computeIfAbsent(v.fuelType().name(), k -> new ArrayList<>()).add(v);
        }
    }
}
