package org.example.commons.dto.create;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BicycleCreateDTO extends VehicleCreateDTO {

    private Integer pedalNumber;

    public BicycleCreateDTO(String plateNumber, Double basePrice, Integer pedalNumber) {
        super(plateNumber, basePrice);
        this.pedalNumber = pedalNumber;
    }
}
