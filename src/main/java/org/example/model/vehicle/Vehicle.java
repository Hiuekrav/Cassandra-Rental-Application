package org.example.model.vehicle;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.model.AbstractEntity;

import java.util.UUID;

@SuperBuilder(toBuilder = true)
@Setter @Getter
public class Vehicle extends AbstractEntity {

    private String plateNumber;

    private Double basePrice;

    private boolean archive;

    private boolean rented;

    private String discriminator;

    public Vehicle(UUID id, String plateNumber, Double basePrice) {
        super(id);
        this.plateNumber = plateNumber;
        this.basePrice = basePrice;
        this.archive = false;
        this.rented = false;
    }

    public Vehicle(VehicleMgd vehicleMgd) {
        super(vehicleMgd.getId());
        this.plateNumber = vehicleMgd.getPlateNumber();
        this.basePrice = vehicleMgd.getBasePrice();
        this.archive = vehicleMgd.isArchive();
        this.rented = vehicleMgd.getRented() == 1;
    }
}
