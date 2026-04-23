package com.hinduprayerlock.backend.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // MONTHLY / YEARLY

    private String description;

    private Double price; // ₹999

    @Column(name = "duration_in_days")
    private Integer durationInDays; // 30 / 365

    private Boolean active; // only active plans visible

    @Column(name = "google_product_id")
    private String googleProductId;

    @Column(name = "razorpay_plan_id")
    private String razorpayPlanId;
}