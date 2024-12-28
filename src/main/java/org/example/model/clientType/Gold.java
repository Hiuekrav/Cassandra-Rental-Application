package org.example.model.clientType;

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
@CqlName(DatabaseConstants.CLIENT_TYPE_TABLE)
@PropertyStrategy(mutable = false)
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
@SuperBuilder(toBuilder = true)
@Setter @Getter
public class Gold extends ClientType {
    public Gold(UUID id, Double discount, Integer maxVehicles) {
        super(id, DatabaseConstants.CLIENT_TYPE_GOLD_DISCRIMINATOR, discount, maxVehicles);
    }

    public Gold(UUID id, String discriminator, Double discount, Integer maxVehicles) {
        super(id, discriminator, discount, maxVehicles);
    }
}
