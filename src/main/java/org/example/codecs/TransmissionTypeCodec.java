package org.example.codecs;

import com.datastax.oss.driver.api.core.ProtocolVersion;
import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodecs;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.example.model.vehicle.Car;
import org.example.model.vehicle.Car.TransmissionType;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class TransmissionTypeCodec implements TypeCodec<TransmissionType> {

    //public TransmissionTypeCodec() {
    //    super(TypeCodecs.TEXT, GenericType.of(Car.TransmissionType.class));
    //}
    //
    //@Nullable
    //@Override
    //protected Car.TransmissionType innerToOuter(@Nullable String s) {
    //    return s == null ? null : Car.TransmissionType.valueOf(s.toUpperCase());
    //}
    //
    //@Nullable
    //@Override
    //protected String outerToInner(@Nullable Car.TransmissionType transmissionType) {
    //    return transmissionType == null ? null : transmissionType.name().toLowerCase();
    //}


    @Override
    public GenericType<TransmissionType> getJavaType() {
        return GenericType.of(TransmissionType.class);
    }

    @Override
    public DataType getCqlType() {
        return DataTypes.TEXT;
    }

    @Override
    public ByteBuffer encode(TransmissionType value, ProtocolVersion protocolVersion) {
        return value == null ? null : ByteBuffer.wrap(value.name().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public TransmissionType decode(ByteBuffer bytes, ProtocolVersion protocolVersion) {
        return bytes == null ? null : TransmissionType.valueOf(StandardCharsets.UTF_8.decode(bytes).toString());
    }

    @Override
    public String format(TransmissionType value) {
        return value == null ? null : value.name();
    }

    @Override
    public TransmissionType parse(String value) {
        return value == null || value.isEmpty() ? null : TransmissionType.valueOf(value);
    }
}