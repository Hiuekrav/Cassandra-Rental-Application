package org.example.repositories.implementations;
import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspace;
import lombok.Getter;
import org.example.codecs.TransmissionTypeCodec;
import org.example.model.AbstractEntity;
import org.example.repositories.interfaces.IObjectRepository;
import org.example.utils.consts.DatabaseConstants;

import java.net.InetSocketAddress;
import java.util.*;


import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createKeyspace;

@Getter
public abstract class ObjectRepository {

    private CqlSession session;

    public ObjectRepository() {
        this.session = CqlSession.builder( )
                .addContactPoint(new InetSocketAddress("cassandra1", 9042))
                .addContactPoint(new InetSocketAddress("cassandra2", 9043))
                .withLocalDatacenter("dc1")
                .withAuthCredentials("cassandra", "cassandrapassword")
                .build();


        CreateKeyspace keyspace = createKeyspace(CqlIdentifier.fromCql(DatabaseConstants.RENT_A_CAR_NAMESPACE))
                .ifNotExists()
                .withSimpleStrategy(2)
                .withDurableWrites(true);
        SimpleStatement createKeyspace = keyspace.build();
        session.execute(createKeyspace);
        session.close();

        this.session = CqlSession.builder( )
                .addContactPoint(new InetSocketAddress("cassandra1", 9042))
                .addContactPoint(new InetSocketAddress("cassandra2", 9043))
                .withLocalDatacenter("dc1")
                .withAuthCredentials("cassandra", "cassandrapassword")
                .withKeyspace(CqlIdentifier.fromCql(DatabaseConstants.RENT_A_CAR_NAMESPACE))
                .addTypeCodecs(new TransmissionTypeCodec())
                .build();
    }
}
