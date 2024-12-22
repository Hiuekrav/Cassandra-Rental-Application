package org.example.model.vehicle;


import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.NamingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.PropertyStrategy;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.utils.consts.DatabaseConstants;


import java.util.UUID;

@Entity(defaultKeyspace = DatabaseConstants.RENT_A_CAR_NAMESPACE)
@CqlName(DatabaseConstants.VEHICLE_TABLE)
@PropertyStrategy(mutable = false)
@NamingStrategy(convention = NamingConvention.LOWER_CAMEL_CASE)
@SuperBuilder(toBuilder = true)
@Setter @Getter
public class Bicycle extends Vehicle {

    @CqlName(DatabaseConstants.BICYCLE_PEDAL_NUMBER)
    private Integer pedalsNumber;

    public Bicycle(UUID id, String plateNumber, Double basePrice, Integer pedalsNumber) {
        super(id, plateNumber, basePrice, DatabaseConstants.MOPED_DISCRIMINATOR);
        this.pedalsNumber = pedalsNumber;
    }

    public Bicycle(UUID id,Integer pedalsNumber, String plateNumber, Double basePrice, boolean archive, boolean rented, String discriminator) {
        super(id, plateNumber, basePrice, archive, rented, discriminator);
        this.pedalsNumber = pedalsNumber;
    }
}
