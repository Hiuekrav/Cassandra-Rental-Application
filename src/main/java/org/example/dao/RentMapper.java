package org.example.dao;

import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.DaoTable;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;

@Mapper
public interface RentMapper {
    @DaoFactory
    RentDao getRentDao(@DaoKeyspace String keyspace, @DaoTable String table);

}
