package org.example.providers;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.entity.EntityHelper;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import org.example.model.Client;
import org.example.model.clientType.ClientType;
import org.example.utils.consts.DatabaseConstants;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;

public class ClientOperationsProvider {

    private final CqlSession session;
    private EntityHelper<Client> clientEntityHelper;

    public ClientOperationsProvider(MapperContext context, EntityHelper<Client> clientEntityHelper) {
        this.session = context.getSession();
        this.clientEntityHelper = clientEntityHelper;
    }

    private Client toClient(Row row) {
        return new Client(
                row.getUuid(DatabaseConstants.ID),
                row.getString(DatabaseConstants.CLIENT_FIRST_NAME),
                row.getString(DatabaseConstants.CLIENT_LAST_NAME),
                row.getString(DatabaseConstants.CLIENT_EMAIL),
                row.getUuid(DatabaseConstants.CLIENT_CLIENT_TYPE_ID),
                row.getString(DatabaseConstants.CLIENT_CITY_NAME),
                row.getString(DatabaseConstants.CLIENT_STREET_NAME),
                row.getString(DatabaseConstants.CLIENT_STREET_NUMBER),
                row.getInt(DatabaseConstants.CLIENT_ACTIVE_RENTS)
        );
    }

    public boolean create(Client client) {

        // assign created client to corresponding email
        SimpleStatement insertEmailTable = QueryBuilder.insertInto(DatabaseConstants.CLIENT_BY_EMAIL_TABLE)
                .value(DatabaseConstants.CLIENT_EMAIL, literal(client.getEmail()))
                .value(DatabaseConstants.ID, literal(client.getId()))
                .ifNotExists()
                .build();
        boolean emailResult = session.execute(insertEmailTable).wasApplied();
        if (!emailResult) return false;

        BoundStatement insertClient = session.prepare(clientEntityHelper.insert().build())
                .bind()
                .setUuid(DatabaseConstants.ID, client.getId())
                .setString(DatabaseConstants.CLIENT_FIRST_NAME, client.getFirstName())
                .setString(DatabaseConstants.CLIENT_LAST_NAME, client.getLastName())
                .setString(DatabaseConstants.CLIENT_EMAIL, client.getEmail())
                .setString(DatabaseConstants.CLIENT_CITY_NAME, client.getCityName())
                .setString(DatabaseConstants.CLIENT_STREET_NAME, client.getStreetName())
                .setString(DatabaseConstants.CLIENT_STREET_NUMBER, client.getStreetNumber())
                .setUuid(DatabaseConstants.CLIENT_CLIENT_TYPE_ID, client.getClientTypeId())
                .setInt(DatabaseConstants.CLIENT_ACTIVE_RENTS, client.getActiveRents());

        // find client type of created client
        SimpleStatement findClientType = QueryBuilder.selectFrom(DatabaseConstants.CLIENT_TYPE_TABLE)
                .column(DatabaseConstants.CLIENT_TYPE_DISCRIMINATOR)
                .where(Relation.column(DatabaseConstants.ID).isEqualTo(literal(client.getClientTypeId())))
                .build();
        Row foundClientTypeRow = session.execute(findClientType).one();
        String clientType = foundClientTypeRow.getString(DatabaseConstants.CLIENT_TYPE_DISCRIMINATOR);

        // assign created client to corresponding type
        SimpleStatement insertClientType = QueryBuilder
                .insertInto(DatabaseConstants.CLIENT_BY_CLIENT_TYPE_TABLE)
                .value(DatabaseConstants.CLIENT_TYPE_DISCRIMINATOR, literal(clientType))
                .value(DatabaseConstants.ID, literal(client.getId()))
                .build();

        BatchStatement clientStatements = BatchStatement.builder(BatchType.LOGGED)
                .addStatements(insertClient, insertClientType).build();

        return session.execute(clientStatements).wasApplied();
    }

    public Client findByEmail(String email) {
        SimpleStatement findEmail = QueryBuilder.selectFrom(DatabaseConstants.CLIENT_BY_EMAIL_TABLE)
                .all().where(Relation.column(DatabaseConstants.CLIENT_EMAIL).isEqualTo(literal(email))).build();
        Row foundEmail = session.execute(findEmail).one();
        if (foundEmail == null) {
            throw new RuntimeException("No client found for email: " + email);
        }

        SimpleStatement findClient = QueryBuilder.selectFrom(DatabaseConstants.CLIENT_TABLE)
                .all()
                .where(Relation.column(DatabaseConstants.ID)
                .isEqualTo(literal(foundEmail.getUuid(DatabaseConstants.ID))))
                .build();

        Row foundClient = session.execute(findClient).one();
        return toClient(foundClient);
    }

    public List<UUID> findByType(String type) {
        SimpleStatement findByType = QueryBuilder.selectFrom(DatabaseConstants.CLIENT_BY_CLIENT_TYPE_TABLE)
                .column(DatabaseConstants.ID)
                .where(Relation.column(DatabaseConstants.CLIENT_TYPE_DISCRIMINATOR)
                .isEqualTo(literal(type))).build();
        List<Row> foundType = session.execute(findByType).all();
        return foundType.stream().map(row -> row.getUuid(DatabaseConstants.ID)).collect(Collectors.toList());
    }

    public boolean increaseActiveRentsNumber(UUID id, int number, int maxRents) {
        // increase the counter only if its value is lesser than max rents,
        SimpleStatement changeNumber;
        changeNumber = QueryBuilder
                .update(DatabaseConstants.CLIENT_TABLE)
                .increment(DatabaseConstants.CLIENT_ACTIVE_RENTS, literal(number))
                .where(Relation.column(DatabaseConstants.ID).isEqualTo(literal(id)))
                .ifColumn(DatabaseConstants.CLIENT_ACTIVE_RENTS).isLessThan(literal(maxRents))
                .build();
        return session.execute(changeNumber).wasApplied();
    }

    public boolean decreaseActiveRentsNumber(UUID id, int number) {
        // decrease the counter only if it is greater or equal zero
        SimpleStatement changeNumber;
        changeNumber = QueryBuilder
                .update(DatabaseConstants.CLIENT_TABLE)
                .increment(DatabaseConstants.CLIENT_ACTIVE_RENTS, literal(number))
                .where(Relation.column(DatabaseConstants.ID).isEqualTo(literal(id)))
                .ifColumn(DatabaseConstants.CLIENT_ACTIVE_RENTS).isGreaterThan(literal(0))
                .build();
        return session.execute(changeNumber).wasApplied();
    }



    public boolean changeClientEmail(Client client, String newEmail) {

        // Update email in main client table
        SimpleStatement updateClientEmail = QueryBuilder.update(DatabaseConstants.CLIENT_TABLE)
                .setColumn(DatabaseConstants.CLIENT_EMAIL, literal(newEmail))
                .where(Relation.column(DatabaseConstants.ID).isEqualTo(literal(client))).build();

        //delete row in table used for searching by email (you can't modify primary key)
        SimpleStatement deleteClientEmailRow = QueryBuilder
                .deleteFrom(DatabaseConstants.CLIENT_BY_EMAIL_TABLE)
                .where(Relation.column(DatabaseConstants.ID).isEqualTo(literal(client.getId())))
                .build();

        // insert row with new email
        SimpleStatement insertClientEmailRow = QueryBuilder.insertInto(DatabaseConstants.CLIENT_BY_EMAIL_TABLE)
                .value(DatabaseConstants.CLIENT_EMAIL, literal(newEmail))
                .value(DatabaseConstants.ID, literal(client.getId()))
                .build();

        BatchStatement batchStatement = BatchStatement.builder(BatchType.LOGGED)
                .addStatements(updateClientEmail, deleteClientEmailRow, insertClientEmailRow)
                .build();
        return session.execute(batchStatement).wasApplied();
    }

    public boolean changeClientType(Client client, ClientType newClientType) {

        // update UUID of client type in the main client table
        SimpleStatement updateClientTypeId = QueryBuilder.update(DatabaseConstants.CLIENT_TABLE)
                .setColumn(DatabaseConstants.CLIENT_CLIENT_TYPE_ID, literal(newClientType.getId()))
                .where(Relation.column(DatabaseConstants.ID).isEqualTo(literal(client.getId()))).build();

        SimpleStatement findOldType = QueryBuilder.selectFrom(DatabaseConstants.CLIENT_TYPE_TABLE)
                .column(DatabaseConstants.CLIENT_TYPE_DISCRIMINATOR)
                .where(Relation.column(DatabaseConstants.ID)
                        .isEqualTo(literal(client.getClientTypeId())))
                .build();
        Row foundOldType = session.execute(findOldType).one();
        String oldType = foundOldType.getString(DatabaseConstants.CLIENT_TYPE_DISCRIMINATOR);

        // update client type discriminator in table used for searching clients by type
        SimpleStatement deleteTypeRow = QueryBuilder.deleteFrom(DatabaseConstants.CLIENT_BY_CLIENT_TYPE_TABLE)
                .where(Relation.column(DatabaseConstants.ID).isEqualTo(literal(client.getId())))
                .where(Relation.column(DatabaseConstants.CLIENT_TYPE_DISCRIMINATOR).isEqualTo(literal(oldType)))
                .build();

        SimpleStatement insertTypeRow = QueryBuilder.insertInto(DatabaseConstants.CLIENT_BY_CLIENT_TYPE_TABLE)
                .value(DatabaseConstants.CLIENT_TYPE_DISCRIMINATOR, literal(newClientType.getDiscriminator()))
                .value(DatabaseConstants.ID, literal(client.getId()))
                .build();

        BatchStatement batchStatement = BatchStatement.builder(BatchType.LOGGED)
                .addStatements(updateClientTypeId, deleteTypeRow, insertTypeRow).build();
        return session.execute(batchStatement).wasApplied();
    }


}
