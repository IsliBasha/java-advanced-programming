package com.university.travelbookingapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/** JPA entity representing a travel booking. */
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Customer name is required")
    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @NotBlank(message = "Destination is required")
    @Column(nullable = false)
    private String destination;

    @NotNull(message = "Departure date is required")
    @Column(name = "departure_date", nullable = false)
    private LocalDate departureDate;

    @NotNull(message = "Return date is required")
    @Column(name = "return_date", nullable = false)
    private LocalDate returnDate;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status is required")
    @Column(nullable = false)
    private BookingStatus status;

    @Positive(message = "Number of travellers must be positive")
    @Column(name = "number_of_travellers", nullable = false)
    private Integer numberOfTravellers;

    protected Booking() {}

    public Booking(String customerName, String destination, LocalDate departureDate, LocalDate returnDate,
                   BigDecimal price, BookingStatus status, Integer numberOfTravellers) {
        this.customerName = customerName;
        this.destination = destination;
        this.departureDate = departureDate;
        this.returnDate = returnDate;
        this.price = price;
        this.status = status;
        this.numberOfTravellers = numberOfTravellers;
    }

    public Long getId() { return id; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public LocalDate getDepartureDate() { return departureDate; }
    public void setDepartureDate(LocalDate departureDate) { this.departureDate = departureDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public Integer getNumberOfTravellers() { return numberOfTravellers; }
    public void setNumberOfTravellers(Integer numberOfTravellers) { this.numberOfTravellers = numberOfTravellers; }
}
