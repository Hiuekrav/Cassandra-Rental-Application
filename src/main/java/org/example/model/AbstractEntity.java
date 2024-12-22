package org.example.model;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.utils.consts.DatabaseConstants;

import java.io.Serializable;
import java.util.UUID;

@SuperBuilder(toBuilder = true)
@Getter
public abstract class AbstractEntity implements Serializable {

    @PartitionKey
    @CqlName(DatabaseConstants.ID)
    private UUID id;

    public AbstractEntity(UUID id) {
        this.id = id;
    }
}
