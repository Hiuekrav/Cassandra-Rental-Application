package org.example.dao;

import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.DaoTable;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;

@Mapper
public interface VehicleMapper {
    @DaoFactory
    VehicleDao getVehicleDao(@DaoKeyspace String keyspace, @DaoTable String table);

}
