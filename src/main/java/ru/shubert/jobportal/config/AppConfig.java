package ru.shubert.jobportal.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.wicket.protocol.http.WebApplication;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import ru.shubert.jobportal.strategy.CustomNamingStrategy;
import ru.shubert.jobportal.strategy.TestDataLoader;
import ru.shubert.jobportal.web.JobPortalWebApplication;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.IOException;

/**
 * Let's give a try to new Spring configuration idea.
 * http://blog.springsource.com/2011/06/10/spring-3-1-m2-configuration-enhancements/
 */

@Configuration
@PropertySource("classpath:db.conf")
@ComponentScan("ru.shubert.jobportal")
@EnableTransactionManagement
public class AppConfig implements TransactionManagementConfigurer {

    @Value("${jdbc.driverClassName}")
    String driverClass;

    @Value("${jdbc.url}")
    String jdbcUrl;

    @Value("${jdbc.username}")
    String user;

    @Value("${jdbc.password}")
    String password;

    @Value("${hibernate.dialect}")
    String dialect;

    @Value("${dbunit.dataTypeFactoryName}")
    String typeFactory;

    @SuppressWarnings("ContextJavaBeanUnresolvedMethodsInspection")
    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
        ComboPooledDataSource ds = new ComboPooledDataSource();
        try {
            ds.setDriverClass(driverClass);
            ds.setJdbcUrl(jdbcUrl);
            ds.setUser(user);
            ds.setPassword(password);
            ds.setTestConnectionOnCheckin(true);
            ds.setIdleConnectionTestPeriod(60);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
            return null;
        }
        return ds;
    }

    @Bean
    public SessionFactory sessionFactory() {
        LocalSessionFactoryBuilder ls = new LocalSessionFactoryBuilder(dataSource());
        ls.scanPackages("ru.shubert.jobportal.model") //scanPackages
                .setNamingStrategy(new CustomNamingStrategy())
                .setProperty("hibernate.hbm2ddl.auto", "update")
                .setProperty("hibernate.dialect", dialect)
                .setProperty("hibernate.show_sql", "true");

        // MUST BE TURNED OFF http://stackoverflow.com/questions/4138663/spring-hibernate-and-declarative-transaction-implementation-there-is-no-active
        //.setProperty("current_session_context_class", "thread")
        //.setProperty("hibernate.current_session_context_class", "thread");
        return ls.buildSessionFactory();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        HibernateTransactionManager tx = new HibernateTransactionManager();
        tx.setSessionFactory(sessionFactory());
        tx.setNestedTransactionAllowed(false);
        return tx;
    }

    @Bean
    public WebApplication application() {
        return new JobPortalWebApplication();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return transactionManager();
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslationPostProcessor(){
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public HibernateExceptionTranslator exceptionTranslator(){
        return new HibernateExceptionTranslator();
    }


    @Bean
    public TestDataLoader testDataLoader() throws IOException, DataSetException {
        TestDataLoader loader = new TestDataLoader();
        loader.setDataSource(dataSource());
        loader.setSetUpOperation("CLEAN_INSERT");

        ClassPathResource resource = new ClassPathResource("sampledataFlat.xml");
        IDataSet consumer = new FlatXmlDataSetBuilder()
                .setDtdMetadata(false)
                .setColumnSensing(true)
                .build(resource.getFile());

        loader.setConsumer(consumer);

        try {
            Class cl = Class.forName(typeFactory);
            loader.setFactory((IDataTypeFactory) cl.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return loader;
    }


}
