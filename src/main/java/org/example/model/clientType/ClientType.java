package org.example.model.clientType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.mgd.clientType.ClientTypeMgd;
import org.example.model.AbstractEntity;

import java.util.UUID;
@SuperBuilder(toBuilder = true)
@Getter @Setter
public class ClientType extends AbstractEntity {
    private Double discount;
    private Integer maxVehicles;

    public ClientType(UUID id, Double discount, Integer maxVehicles) {
        super(id);
        this.discount = discount;
        this.maxVehicles = maxVehicles;
    }


    public ClientType(ClientTypeMgd clientTypeMgd) {
        super(clientTypeMgd.getId());
        this.discount = clientTypeMgd.getDiscount();
        this.maxVehicles = clientTypeMgd.getMaxVehicles();
    }
}
