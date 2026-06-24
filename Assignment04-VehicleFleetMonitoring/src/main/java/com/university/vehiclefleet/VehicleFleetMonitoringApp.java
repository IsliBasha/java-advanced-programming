package com.university.vehiclefleet;

import com.university.vehiclefleet.model.FuelType;
import com.university.vehiclefleet.model.Vehicle;
import com.university.vehiclefleet.service.FleetService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Entry point for Assignment 04 – Vehicle Fleet Monitoring System.
 */
public class VehicleFleetMonitoringApp {

    public static void main(String[] args) {
        FleetService service = new FleetService();

        // ── Seed data ─────────────────────────────────────────────────────
        service.addVehicle(new Vehicle("V001", "AB12 CDE", "Toyota",  "Corolla",    FuelType.PETROL,  45000, LocalDate.of(2024, 6, 15), true));
        service.addVehicle(new Vehicle("V002", "XY34 FGH", "Ford",    "Transit",    FuelType.DIESEL,  92000, LocalDate.of(2024, 4, 1),  true));
        service.addVehicle(new Vehicle("V003", "LM56 NOP", "Tesla",   "Model 3",    FuelType.ELECTRIC,12000, LocalDate.of(2025, 1, 10), true));
        service.addVehicle(new Vehicle("V004", "QR78 STU", "Toyota",  "Hilux",      FuelType.DIESEL,  130000,LocalDate.of(2024, 3, 1),  true));
        service.addVehicle(new Vehicle("V005", "VW90 XYZ", "Honda",   "Civic",      FuelType.PETROL,  67000, LocalDate.of(2024, 8, 20), false));
        service.addVehicle(new Vehicle("V006", "PQ11 RST", "BMW",     "330e",       FuelType.HYBRID,  28000, LocalDate.of(2024, 9, 5),  true));
        service.addVehicle(new Vehicle("V007", "EF22 GHI", "Ford",    "Ranger",     FuelType.DIESEL,  55000, LocalDate.of(2024, 7, 12), true));

        printHeader("FULL FLEET");
        printVehicles(service.getAllVehicles());

        printHeader("FUEL TYPES Set");
        service.getFuelTypes().stream().sorted().forEach(f -> System.out.println("  " + f));

        printHeader("VEHICLES BY FUEL TYPE Map<String, List<Vehicle>>");
        service.getVehiclesByFuelType().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> {
                    System.out.printf("  %-10s → ", e.getKey());
                    System.out.println(e.getValue().stream().map(v -> v.registrationPlate()).toList());
                });

        printHeader("Optional LOOKUP: V003");
        service.findById("V003").ifPresentOrElse(
                v -> System.out.printf("  %s %s (Electric, %d mi)%n", v.make(), v.model(), v.mileage()),
                () -> System.out.println("  Not found."));

        printHeader("MILEAGE FILTER: > 60,000");
        printVehicles(service.findByMileageAbove(60000));

        printHeader("STREAM MAP → VehicleSummaryDto");
        service.toSummaries().forEach(dto ->
                System.out.printf("  %-12s | %-20s | %,d mi%n",
                        dto.registrationPlate(), dto.makeModel(), dto.mileage()));

        printHeader("MODEL PATTERN SEARCH: \"toyota\"");
        service.searchByModelPattern("toyota").forEach(v ->
                System.out.printf("  %s %s (%s)%n", v.make(), v.model(), v.registrationPlate()));

        printHeader("SERVICE DUE BEFORE 2024-07-01");
        List<Vehicle> serviceDue = service.findServiceDueBefore(LocalDate.of(2024, 7, 1));
        printVehicles(serviceDue);
        System.out.println("  Count overdue: " + serviceDue.size());

        printHeader("FILTER BY FUEL TYPE: DIESEL");
        printVehicles(service.findByFuelType(FuelType.DIESEL));
    }

    private static void printHeader(String title) {
        System.out.println();
        System.out.println("═".repeat(70));
        System.out.println("  " + title);
        System.out.println("─".repeat(70));
    }

    private static void printVehicles(List<Vehicle> list) {
        System.out.printf("  %-6s %-12s %-10s %-12s %-10s %8s  %s%n",
                "ID", "Plate", "Make", "Model", "Fuel", "Mileage", "Next Service");
        System.out.println("  " + "─".repeat(68));
        list.forEach(v -> System.out.printf(
                "  %-6s %-12s %-10s %-12s %-10s %,8d  %s%n",
                v.vehicleId(), v.registrationPlate(), v.make(), v.model(),
                v.fuelType(), v.mileage(), v.nextServiceDate()));
    }
}
