package org.example.providers;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.entity.EntityHelper;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import org.example.model.clientType.ClientType;
import org.example.model.clientType.Default;
import org.example.model.clientType.Gold;
import org.example.model.clientType.Silver;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;

public class ClientTypeProvider {

    private final CqlSession session;
    private EntityHelper<ClientType> clientTypeHelper;

    public ClientTypeProvider(MapperContext context, EntityHelper<ClientType> clientTypeHelper) {
        this.session = context.getSession();
        this.clientTypeHelper = clientTypeHelper;
    }

    Default toDefault(Row row) {
        return new Default(
                row.getUuid(DatabaseConstants.ID),
                row.getString(DatabaseConstants.CLIENT_TYPE_DISCRIMINATOR),
                row.getDouble(DatabaseConstants.CLIENT_TYPE_DISCOUNT),
                row.getInt(DatabaseConstants.CLIENT_TYPE_MAX_VEHICLES)
        );
    }
    Silver toSilver(Row row) {
        return new Silver(
                row.getUuid(DatabaseConstants.ID),
                row.getString(DatabaseConstants.CLIENT_TYPE_DISCRIMINATOR),
                row.getDouble(DatabaseConstants.CLIENT_TYPE_DISCOUNT),
                row.getInt(DatabaseConstants.CLIENT_TYPE_MAX_VEHICLES)
        );
    }

    Gold toGold(Row row) {
        return new Gold(
                row.getUuid(DatabaseConstants.ID),
                row.getString(DatabaseConstants.CLIENT_TYPE_DISCRIMINATOR),
                row.getDouble(DatabaseConstants.CLIENT_TYPE_DISCOUNT),
                row.getInt(DatabaseConstants.CLIENT_TYPE_MAX_VEHICLES)
        );
    }


    public ClientType findById(UUID id) {
        SimpleStatement findById = QueryBuilder.selectFrom(DatabaseConstants.CLIENT_TYPE_TABLE)
                .all()
                .where(Relation.column(DatabaseConstants.ID).isEqualTo(literal(id)))
                .build();

        Row row = session.execute(findById).one();
        if (row == null) {
            return null;
        }

        String discriminator = row.getString(DatabaseConstants.CLIENT_TYPE_DISCRIMINATOR);

        return switch (discriminator) {
            case DatabaseConstants.CLIENT_TYPE_DEFAULT_DISCRIMINATOR -> toDefault(row);
            case DatabaseConstants.CLIENT_TYPE_SILVER_DISCRIMINATOR -> toSilver(row);
            case DatabaseConstants.CLIENT_TYPE_GOLD_DISCRIMINATOR -> toGold(row);
            default -> null;
        };
    }
}
