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
import org.example.utils.consts.DatabaseConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//@Entity(defaultKeyspace = DatabaseConstants.RENT_A_CAR_NAMESPACE)
//@CqlName(DatabaseConstants.CLIENT_TABLE)
//@PropertyStrategy(mutable = false)
//@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
@SuperBuilder(toBuilder = true)
@Getter @Setter
public class Client extends AbstractEntity {

    private String firstName;
    private String lastName;
    private String email;
    private ClientType clientType;
    private String cityName;
    private String streetName;
    private String streetNumber;

    public Client(UUID id, String firstName, String lastName, String email,
                  ClientType clientType, String cityName, String streetName, String streetNumber) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.cityName = cityName;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.clientType = clientType;
    }

}
