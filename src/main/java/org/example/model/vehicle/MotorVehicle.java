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

@CqlName(DatabaseConstants.VEHICLE_TABLE)
@PropertyStrategy(mutable = false)
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
@SuperBuilder(toBuilder = true)
@Setter @Getter
public class MotorVehicle extends Vehicle {

    @CqlName(DatabaseConstants.MOTOR_VEHICLE_ENGINE_DISPLACEMENT)
    private Integer engineDisplacement;

    public MotorVehicle(UUID id, String plateNumber, Double basePrice, Integer engineDisplacement, String discriminator) {
        super(id, plateNumber, basePrice, discriminator);
        this.engineDisplacement = engineDisplacement;
    }

    public MotorVehicle(UUID id, String plateNumber, Double basePrice, boolean archive, boolean rented, String discriminator, Integer engineDisplacement) {
        super(id, plateNumber, basePrice, archive, rented, discriminator);
        this.engineDisplacement = engineDisplacement;
    }
}
