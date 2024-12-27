package org.example.model.vehicle;

import com.datastax.oss.driver.api.mapper.annotations.*;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.model.AbstractEntity;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;

@Entity(defaultKeyspace = DatabaseConstants.RENT_A_CAR_NAMESPACE)
@CqlName(DatabaseConstants.VEHICLE_TABLE)
@PropertyStrategy(mutable = false)
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
@SuperBuilder(toBuilder = true)
@Setter @Getter
public class Vehicle extends AbstractEntity {

    @CqlName(DatabaseConstants.VEHICLE_PLATE_NUMBER)
    private String plateNumber;

    @CqlName(DatabaseConstants.VEHICLE_BASE_PRICE)
    private Double basePrice;

    @CqlName(DatabaseConstants.VEHICLE_ARCHIVE)
    private boolean archive;

    @CqlName(DatabaseConstants.VEHICLE_RENTED)
    private boolean rented;

    @CqlName(DatabaseConstants.VEHICLE_VERSION)
    private Integer version = 0;

    @CqlName(DatabaseConstants.VEHICLE_DISCRIMINATOR)
    @ClusteringColumn
    private String discriminator;

    public Vehicle(UUID id, String plateNumber, Double basePrice, String discriminator) {
        super(id);
        this.plateNumber = plateNumber;
        this.basePrice = basePrice;
        this.archive = false;
        this.rented = false;
        this.discriminator = discriminator;
        version = 0;
    }

    public Vehicle(UUID id, String discriminator, String plateNumber, Double basePrice, boolean archive, boolean rented, Integer version) {
        super(id);
        this.plateNumber = plateNumber;
        this.basePrice = basePrice;
        this.archive = archive;
        this.rented = rented;
        this.discriminator = discriminator;
        this.version = version;
    }
}
