package ru.shubert.jobportal.strategy;

import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.lang.reflect.Field;

/**
 *
 */

@DependsOn()
public class TestDataLoader {
    private static Logger logger = LoggerFactory.getLogger(TestDataLoader.class);

    private DataSource dataSource;
    private DataSourceDatabaseTester dbtester;
    private IDataSet consumer;
    private IDataTypeFactory factory;

    private DatabaseOperation setUpOperation;
    private DatabaseOperation tearDownOperation;


    @SuppressWarnings("UnusedDeclaration")
    public void setTearDownOperation(String tearDownOperation) {
        this.tearDownOperation = getOperationByName(tearDownOperation);
    }

    @PreDestroy
    public void destroy() throws Exception {
        dbtester.onTearDown();
    }

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        dbtester = new DataSourceDatabaseTester(dataSource) {

            @Override
            public IDatabaseConnection getConnection() throws Exception {
                IDatabaseConnection connection = super.getConnection();

                if (factory != null) {
                    connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, factory);
                }

                return connection;
            }
        };

        dbtester.setDataSet(consumer);
        dbtester.setSetUpOperation(setUpOperation);
        dbtester.setTearDownOperation(tearDownOperation);


        // calls for base initialization see DatabaseTestCase#setUp()
        dbtester.onSetup();
    }

    private DatabaseOperation getOperationByName(@NotNull String operation) {
        try {
            Field field = DatabaseOperation.class.getDeclaredField(operation);
            return (DatabaseOperation) field.get(null);
        } catch (Exception e) {
            logger.error("unsupported operation received: " + operation, e);
            throw new RuntimeException(e);
        }
    }

    @Required
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Required
    public void setSetUpOperation(String setUpOperation) {
        this.setUpOperation = getOperationByName(setUpOperation);
    }

    @Required
    public void setConsumer(IDataSet consumer) {
        this.consumer = consumer;
    }

    @Required
    public void setFactory(IDataTypeFactory factory) {
        this.factory = factory;
    }
}
