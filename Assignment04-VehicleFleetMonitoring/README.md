# Assignment 04 – Vehicle Fleet Monitoring System

Maven/Java 21 console application demonstrating Java Collections, `Optional`, and the Stream API for vehicle fleet monitoring.

## Project Structure
```
src/main/java/com/university/vehiclefleet/
├── VehicleFleetMonitoringApp.java     ← entry point / demo
├── model/Vehicle.java                 ← immutable record
├── model/FuelType.java                ← enum (PETROL/DIESEL/ELECTRIC/HYBRID)
├── service/FleetService.java          ← business logic
└── dto/VehicleSummaryDto.java         ← DTO record
```

## How to Run
```bash
mvn test
mvn package && java -jar target/vehicle-fleet-monitoring-1.0.0.jar
```

## Notes for the Professor
- `findServiceDueBefore(LocalDate threshold)` uses `LocalDate.isBefore()` for date comparison — avoids manual epoch arithmetic and is clearly readable.
- `Map<String, List<Vehicle>>` uses the fuel type's enum name (string) as key so the map key is human-readable in console output.
- The `VehicleSummaryDto.makeModel` field concatenates make + model at map time in `toSummaries()` — this is the "stream mapping" transformation required by the assignment.
- `searchByModelPattern` does a case-insensitive search on the concatenated `make + " " + model` string, so searching "toyota" matches "Toyota Corolla" and "Toyota Hilux".
