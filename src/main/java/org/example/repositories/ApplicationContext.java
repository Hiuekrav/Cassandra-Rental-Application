package org.example.repositories;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspace;
import lombok.Getter;
import org.example.codecs.LocalDateTimeCodec;
import org.example.codecs.TransmissionTypeCodec;
import org.example.utils.consts.DatabaseConstants;

import java.net.InetSocketAddress;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createKeyspace;

public class ApplicationContext {

    @Getter
    private static final ApplicationContext context = new ApplicationContext();

    private static CqlSession session = null;

    private ApplicationContext() {
        initSession();
    }

    public static void initSession() {

        session = CqlSession.builder( )
                .addContactPoint(new InetSocketAddress("cassandra1", 9042))
                .addContactPoint(new InetSocketAddress("cassandra2", 9043))
                .addContactPoint(new InetSocketAddress("cassandra3", 9044))
                .withLocalDatacenter("dc1")
                .withAuthCredentials("cassandra", "cassandrapassword")
                .build();

        CreateKeyspace keyspace = createKeyspace(CqlIdentifier.fromCql(DatabaseConstants.RENT_A_CAR_NAMESPACE))
                .ifNotExists()
                .withSimpleStrategy(3)
                .withDurableWrites(true);
        SimpleStatement createKeyspace = keyspace.build();
        session.execute(createKeyspace);
        session.close();

        session = CqlSession.builder( )
                .addContactPoint(new InetSocketAddress("cassandra1", 9042))
                .addContactPoint(new InetSocketAddress("cassandra2", 9043))
                .addContactPoint(new InetSocketAddress("cassandra3", 9044))
                .withLocalDatacenter("dc1")
                .withAuthCredentials("cassandra", "cassandrapassword")
                .withKeyspace(CqlIdentifier.fromCql(DatabaseConstants.RENT_A_CAR_NAMESPACE))
                .addTypeCodecs(new TransmissionTypeCodec())
                .addTypeCodecs(new LocalDateTimeCodec())
                .build();
    }

    public static CqlSession getSession() {
        if (session == null) {
            initSession();
        }
        return session;
    }
}