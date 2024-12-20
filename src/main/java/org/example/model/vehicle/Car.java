package org.example.model.vehicle;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.mgd.vehicle.CarMgd;

import java.util.UUID;

@SuperBuilder(toBuilder = true)
@Getter @Setter
public class Car extends MotorVehicle {

    public enum TransmissionType {
        MANUAL,
        AUTOMATIC
    }

    private TransmissionType transmissionType;

    public Car(UUID id, String plateNumber, Double basePrice, Integer engine_displacement, TransmissionType type) {
        super(id, plateNumber, basePrice, engine_displacement);
        this.transmissionType = type;
    }

    public Car(CarMgd carMgd) {
        super(
            carMgd.getId(),
            carMgd.getPlateNumber(),
            carMgd.getBasePrice(),
            carMgd.getEngineDisplacement()
        );
        this.transmissionType = carMgd.getTransmissionType();
    }






}
