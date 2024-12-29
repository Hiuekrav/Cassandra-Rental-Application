package org.example.model;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.NamingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.PropertyStrategy;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.model.clientType.ClientType;
import org.example.model.vehicle.Vehicle;
import org.example.utils.consts.DatabaseConstants;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity(defaultKeyspace = DatabaseConstants.RENT_A_CAR_NAMESPACE)
@CqlName(DatabaseConstants.RENT_TABLE)
@PropertyStrategy(mutable = false)
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
@SuperBuilder(toBuilder = true)
@Getter @Setter
public class Rent extends AbstractEntity {

    @CqlName(DatabaseConstants.RENT_BEGIN_TIME)
    private LocalDateTime beginTime;

    @CqlName(DatabaseConstants.RENT_END_TIME)
    private LocalDateTime endTime;

    @CqlName(DatabaseConstants.RENT_CLIENT_ID)
    private UUID clientId;

    @CqlName(DatabaseConstants.RENT_VEHICLE_ID)
    private UUID vehicleId;

    @CqlName(DatabaseConstants.RENT_RENT_COST)
    private Double rentCost;

    public Rent(UUID id, LocalDateTime beginTime, LocalDateTime endTime, Client client, Vehicle vehicle) {
        super(id);
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.clientId = client.getId();
        this.vehicleId = vehicle.getId();
        this.rentCost = ChronoUnit.HOURS.between(beginTime, endTime.plusHours(1)) * vehicle.getBasePrice();
    }

    public Rent(UUID id, LocalDateTime beginTime, LocalDateTime endTime, UUID clientId, UUID vehicleId, Double rentCost) {
        super(id);
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.clientId = clientId;
        this.vehicleId = vehicleId;
        this.rentCost = rentCost;
    }

    public void recalculateRentCost(Vehicle vehicle, Client client, ClientType clientType) {
        this.rentCost = ChronoUnit.HOURS.between(beginTime, endTime.plusHours(1)) * vehicle.getBasePrice() - clientType.getDiscount();
    }


    public Rent(UUID id, LocalDateTime endTime, Client client, Vehicle vehicle) {
        super(id);
        this.beginTime = LocalDateTime.now();
        this.endTime = endTime;
        this.clientId = client.getId();
        this.vehicleId = vehicle.getId();
        this.rentCost = ChronoUnit.HOURS.between(beginTime, endTime.plusHours(1)) * vehicle.getBasePrice();
    }
}
