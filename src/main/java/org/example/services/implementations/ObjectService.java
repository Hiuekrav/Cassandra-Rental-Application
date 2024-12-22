package org.example.services.implementations;

import lombok.Getter;
import org.example.services.interfaces.IObjectService;
import org.example.utils.consts.DatabaseConstants;

import java.util.List;

@Getter
public abstract class ObjectService implements IObjectService {

    public ObjectService() {
        initDatabaseConnection();
    }

    @Override
    public void initDatabaseConnection() {
    }
}