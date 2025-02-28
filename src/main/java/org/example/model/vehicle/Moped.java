package org.example.model.vehicle;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.NamingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.PropertyStrategy;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;

@Entity(defaultKeyspace = DatabaseConstants.RENT_A_CAR_NAMESPACE)
@CqlName(DatabaseConstants.VEHICLE_TABLE)
@PropertyStrategy(mutable = false)
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
@SuperBuilder(toBuilder = true)
@Getter @Setter
public class Moped extends MotorVehicle {

    public Moped(UUID id, String plateNumber, Double basePrice, Integer engine_displacement) {
        super(id, plateNumber, basePrice, engine_displacement, DatabaseConstants.MOPED_DISCRIMINATOR);
    }

    public Moped(UUID id, String discriminator, Integer engineDisplacement, String plateNumber,
                 Double basePrice, boolean archive, boolean rented, Integer version) {
        super(id, discriminator, plateNumber, basePrice, archive, rented, engineDisplacement, version);
    }
}
