package org.example.model.clientType;

import com.datastax.oss.driver.api.mapper.annotations.*;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.model.AbstractEntity;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;

@Entity(defaultKeyspace = DatabaseConstants.RENT_A_CAR_NAMESPACE)
@CqlName(DatabaseConstants.CLIENT_TYPE_TABLE)
@PropertyStrategy(mutable = false)
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
@SuperBuilder(toBuilder = true)
@Getter @Setter
public class ClientType extends AbstractEntity {

    @CqlName(DatabaseConstants.CLIENT_TYPE_DISCOUNT)
    private Double discount;

    @CqlName(DatabaseConstants.CLIENT_TYPE_MAX_VEHICLES)
    private Integer maxVehicles;

    @CqlName(DatabaseConstants.CLIENT_TYPE_DISCRIMINATOR)
    @ClusteringColumn
    private String discriminator;

    public ClientType(UUID id, String discriminator, Double discount, Integer maxVehicles) {
        super(id);
        this.discount = discount;
        this.maxVehicles = maxVehicles;
        this.discriminator = discriminator;
    }
}
