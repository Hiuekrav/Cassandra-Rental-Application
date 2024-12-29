package org.example.codecs;

import com.datastax.oss.driver.api.core.ProtocolVersion;
import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class LocalDateTimeCodec implements TypeCodec<LocalDateTime> {

    @Override
    public GenericType<LocalDateTime> getJavaType() {
        return GenericType.LOCAL_DATE_TIME;
    }

    @Override
    public DataType getCqlType() {
        return DataTypes.TIMESTAMP;
    }

    @Override
    public ByteBuffer encode(LocalDateTime value, ProtocolVersion protocolVersion) {
        if (value == null) {
            return null;
        }
        Instant instant = value.toInstant(ZoneOffset.UTC);
        return ByteBuffer.allocate(8).putLong(instant.toEpochMilli()).flip();
    }

    @Override
    public LocalDateTime decode(ByteBuffer bytes, ProtocolVersion protocolVersion) {
        if (bytes == null || bytes.remaining() == 0) {
            return null;
        }
        long timestamp = bytes.getLong(bytes.position());
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC);
    }

    @Override
    public String format(LocalDateTime value) {
        if (value == null) {
            return "NULL";
        }
        return Long.toString(value.toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    @Override
    public LocalDateTime parse(String value) {
        if (value == null || value.isEmpty() || value.equalsIgnoreCase("NULL")) {
            return null;
        }
        try {
            long timestamp = Long.parseLong(value);
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Cannot parse timestamp value: " + value, e);
        }
    }
}
