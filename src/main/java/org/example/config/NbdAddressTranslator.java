package org.example.config;

import com.datastax.oss.driver.api.core.addresstranslation.AddressTranslator;
import com.datastax.oss.driver.api.core.context.DriverContext;

import java.net.InetSocketAddress;

public class NbdAddressTranslator implements AddressTranslator {

    public NbdAddressTranslator(DriverContext dctx) { }

    public InetSocketAddress translate(InetSocketAddress address) {
        String hostAddress = address.getAddress().getHostAddress();
        String hostName = address.getHostName();
        return switch (hostAddress) {
            case "172.16.0.2" -> new InetSocketAddress("cassandra1", 9042);
            case "172.16.0.3" -> new InetSocketAddress("cassandra2", 9043);
            case "172.16.0.4" -> new InetSocketAddress("cassandra3", 9044);
            default -> throw new RuntimeException("wrong address");
        };
    }
    @Override
    public void close () {}
}
