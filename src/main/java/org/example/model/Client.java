package org.example.model;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.NamingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.PropertyStrategy;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;

@Entity(defaultKeyspace = DatabaseConstants.RENT_A_CAR_NAMESPACE)
@CqlName(DatabaseConstants.CLIENT_TABLE)
@PropertyStrategy(mutable = false)
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
@SuperBuilder(toBuilder = true)
@Getter @Setter
public class Client extends AbstractEntity {

    @CqlName(DatabaseConstants.CLIENT_FIRST_NAME)
    private String firstName;

    @CqlName(DatabaseConstants.CLIENT_LAST_NAME)
    private String lastName;

    @CqlName(DatabaseConstants.CLIENT_EMAIL)
    private String email;

    @CqlName(DatabaseConstants.CLIENT_CLIENT_TYPE_ID)
    private UUID clientTypeId;

    @CqlName(DatabaseConstants.CLIENT_CITY_NAME)
    private String cityName;

    @CqlName(DatabaseConstants.CLIENT_STREET_NAME)
    private String streetName;

    @CqlName(DatabaseConstants.CLIENT_STREET_NUMBER)
    private String streetNumber;

    @CqlName(DatabaseConstants.CLIENT_ACTIVE_RENTS)
    private Integer activeRents;

    public Client(UUID id, String firstName, String lastName, String email,
                  UUID clientTypeId, String cityName, String streetName, String streetNumber) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.cityName = cityName;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.clientTypeId = clientTypeId;
        this.activeRents = 0;
    }

    public Client(UUID id, String firstName, String lastName, String email,
                  UUID clientTypeId, String cityName, String streetName, String streetNumber, Integer activeRents) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.cityName = cityName;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.clientTypeId = clientTypeId;
        this.activeRents = activeRents;
    }

}
