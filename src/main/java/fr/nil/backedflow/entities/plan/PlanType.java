package fr.nil.backedflow.entities.plan;

import java.sql.Date;
import java.time.LocalDate;

public enum PlanType {


    FREE(0, 5, "Free Plan"),
    PREMIUM(2.99F, 20, "Premium Plan"),
    ULTIMATE(4.99F, 50, "Ultimate Plan");

    private final float price;
    private final int maxUploadCapacity;
    private final String description;

    PlanType(float price, int maxUploadCapacity, String description) {
        this.price = price;
        this.maxUploadCapacity = maxUploadCapacity;
        this.description = description;
    }

    public Plan toPlan() {

        return Plan.builder()
                .name(this)
                .price(price)
                .maxUploadCapacity(maxUploadCapacity)
                .description(description)
                .startedAt(Date.valueOf(LocalDate.now()))
                .endsAt(Date.valueOf(LocalDate.now().plusMonths(1)))
                .build();
    }
}
