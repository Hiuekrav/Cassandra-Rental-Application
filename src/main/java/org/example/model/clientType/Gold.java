package org.example.model.clientType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@SuperBuilder(toBuilder = true)
@Setter @Getter
public class Gold extends ClientType {
    public Gold(UUID id, Double discount, Integer maxVehicles) {
        super(id, discount, maxVehicles);
    }

}
