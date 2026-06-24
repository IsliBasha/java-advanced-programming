package com.university.vehiclefleet;

import com.university.vehiclefleet.model.FuelType;
import com.university.vehiclefleet.model.Vehicle;
import com.university.vehiclefleet.service.FleetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FleetService Tests")
class FleetServiceTest {

    private FleetService service;
    private static final LocalDate BASE = LocalDate.of(2024, 1, 1);

    @BeforeEach
    void setUp() {
        service = new FleetService();
        service.addVehicle(new Vehicle("V001", "AB12 CDE", "Toyota", "Corolla", FuelType.PETROL,  45000, BASE.plusMonths(6), true));
        service.addVehicle(new Vehicle("V002", "XY34 FGH", "Ford",   "Transit", FuelType.DIESEL,  92000, BASE.plusMonths(2), true));
        service.addVehicle(new Vehicle("V003", "LM56 NOP", "Tesla",  "Model 3", FuelType.ELECTRIC,12000, BASE.plusYears(1),  true));
    }

    @Test @DisplayName("addVehicle populates all collections")
    void testAdd() {
        assertEquals(3, service.getAllVehicles().size());
        assertTrue(service.getFuelTypes().contains("DIESEL"));
        assertEquals(1, service.getVehiclesByFuelType().get("ELECTRIC").size());
    }

    @Test @DisplayName("findById returns present Optional for known ID")
    void testFindById() {
        assertTrue(service.findById("V002").isPresent());
        assertEquals("Ford", service.findById("V002").get().make());
        assertTrue(service.findById("V999").isEmpty());
    }

    @Test @DisplayName("findByMileageAbove filters correctly")
    void testMileageAbove() {
        List<Vehicle> result = service.findByMileageAbove(50000);
        assertEquals(1, result.size());
        assertEquals("V002", result.get(0).vehicleId());
    }

    @Test @DisplayName("toSummaries maps make+model correctly")
    void testToSummaries() {
        var summaries = service.toSummaries();
        assertEquals(3, summaries.size());
        assertTrue(summaries.stream().anyMatch(s -> s.makeModel().equals("Toyota Corolla")));
    }

    @Test @DisplayName("searchByModelPattern does case-insensitive partial match")
    void testSearchByPattern() {
        List<Vehicle> results = service.searchByModelPattern("toyota");
        assertEquals(1, results.size());
        assertEquals("Corolla", results.get(0).model());
    }

    @Test @DisplayName("findServiceDueBefore returns overdue vehicles")
    void testServiceDue() {
        List<Vehicle> due = service.findServiceDueBefore(BASE.plusMonths(5));
        assertEquals(1, due.size());
        assertEquals("V002", due.get(0).vehicleId());
    }

    @Test @DisplayName("findByFuelType returns correct subset")
    void testFuelType() {
        assertEquals(1, service.findByFuelType(FuelType.PETROL).size());
        assertEquals(0, service.findByFuelType(FuelType.HYBRID).size());
    }

    @Test @DisplayName("removeVehicle updates derived collections")
    void testRemove() {
        service.removeVehicle("V002");
        assertFalse(service.getFuelTypes().contains("DIESEL"));
        assertEquals(2, service.getAllVehicles().size());
    }

    @Test @DisplayName("Vehicle rejects negative mileage")
    void testNegativeMileage() {
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle("V99", "XX", "X", "X", FuelType.PETROL, -1, BASE, true));
    }
}
