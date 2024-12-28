package org.example.providers;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.entity.EntityHelper;
import org.example.model.Client;
import org.example.model.vehicle.Bicycle;

public class ClientOperationsProvider {

    private final CqlSession session;
    private EntityHelper<Client> bicycleEntityHelper;

    public ClientOperationsProvider(MapperContext context, EntityHelper<Client> bicycleEntityHelper) {
        this.session = context.getSession();
        this.bicycleEntityHelper = bicycleEntityHelper;
    }


}
