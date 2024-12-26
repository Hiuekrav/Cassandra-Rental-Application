package org.example.model.vehicle;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.NamingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.PropertyStrategy;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;

@Entity(defaultKeyspace = DatabaseConstants.RENT_A_CAR_NAMESPACE)
@CqlName(DatabaseConstants.VEHICLE_TABLE)
@PropertyStrategy(mutable = false)
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
@SuperBuilder(toBuilder = true)
@Getter @Setter
public class Car extends MotorVehicle {

    public enum TransmissionType {
        MANUAL,
        AUTOMATIC
    }

    @CqlName(DatabaseConstants.CAR_TRANSMISSION_TYPE)
    private TransmissionType transmissionType;

    public Car(UUID id, String plateNumber, Double basePrice, Integer engine_displacement, TransmissionType type) {
        super(id, plateNumber, basePrice, engine_displacement, DatabaseConstants.CAR_DISCRIMINATOR);
        this.transmissionType = type;
    }

    public Car(UUID id, TransmissionType transmissionType, Integer engineDisplacement,
               String plateNumber, Double basePrice, boolean archive, boolean rented, String discriminator) {
        super(id, plateNumber, basePrice, archive, rented, discriminator, engineDisplacement);
        this.transmissionType = transmissionType;
    }
}
