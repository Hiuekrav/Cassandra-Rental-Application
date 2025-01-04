package org.example.repositories.implementations;
import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspace;
import lombok.Getter;
import org.example.codecs.LocalDateTimeCodec;
import org.example.codecs.TransmissionTypeCodec;
import org.example.repositories.ApplicationContext;
import org.example.utils.consts.DatabaseConstants;

import java.net.InetSocketAddress;


import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createKeyspace;

@Getter
public abstract class ObjectRepository {

    public ObjectRepository() {}

    public CqlSession getSession() {
        return ApplicationContext.getSession();
    }
}
