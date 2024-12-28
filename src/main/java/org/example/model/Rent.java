package org.example.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.model.clientType.ClientType;
import org.example.model.vehicle.Vehicle;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

//@Entity(defaultKeyspace = DatabaseConstants.RENT_A_CAR_NAMESPACE)
//@CqlName(DatabaseConstants.RENT_TABLE)
//@PropertyStrategy(mutable = false)
//@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
@SuperBuilder(toBuilder = true)
@Getter @Setter
public class Rent extends AbstractEntity {

    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private Client client;
    private Vehicle vehicle;
    private Double rentCost;
    private boolean active;

    public Rent(UUID id, LocalDateTime beginTime, LocalDateTime endTime, Client client, Vehicle vehicle, boolean active) {
        super(id);
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.client = client;
        this.vehicle = vehicle;
        this.active = active;
        this.rentCost = ChronoUnit.HOURS.between(beginTime, endTime.plusHours(1)) * vehicle.getBasePrice();
    }

    //public void recalculateRentCost() {
    //    this.rentCost = ChronoUnit.HOURS.between(beginTime, endTime.plusHours(1)) * vehicle.getBasePrice() - client.getClientTypeId().getDiscount();
    //}


    //public Rent(UUID id, LocalDateTime endTime, Client client, Vehicle vehicle) {
    //    super(id);
    //    this.beginTime = LocalDateTime.now();
    //    this.endTime = endTime;
    //    this.client = client;
    //    this.vehicle = vehicle;
    //    this.active = true;
    //    this.rentCost = ChronoUnit.HOURS.between(beginTime, endTime.plusHours(1)) * vehicle.getBasePrice() - client.getClientTypeId().getDiscount();
    //}
}
