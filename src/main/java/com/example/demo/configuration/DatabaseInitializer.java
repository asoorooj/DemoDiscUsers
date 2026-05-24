package com.example.demo.configuration;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import com.example.demo.repository.UserRepository;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

@Configuration
public class DatabaseInitializer {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.driver-class-name}")
    private String dbDriver;

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private HikariDataSource dataSource;
    private UserRepository userRepository;
    private boolean databaseConnected = false;

    // Run right after bean is constructed
    @PostConstruct
    public void initializeDatabase() {
        try {
            System.out.println("🔄 Attempting to connect to database...");

            this.dataSource = DataSourceBuilder.create()
                    .type(HikariDataSource.class)
                    .driverClassName(dbDriver)
                    .url(dbUrl)
                    .username(dbUsername)
                    .password(dbPassword)
                    .build();

            this.dataSource.setMaximumPoolSize(3);
            this.dataSource.setMinimumIdle(0);
            this.dataSource.setConnectionTimeout(3000);
            this.dataSource.setIdleTimeout(30000);

            LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
            factoryBean.setDataSource(dataSource);
            factoryBean.setPackagesToScan("com.example.demo.entity");
            factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

            Properties props = new Properties();
            props.put("hibernate.hbm2ddl.auto", "update");
            props.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
//            props.put("hibernate.show_sql", "true");
//            props.put("hibernate.format_sql", "true");
            factoryBean.setJpaProperties(props);

            factoryBean.afterPropertiesSet();
            this.entityManagerFactory = factoryBean.getObject();

            this.entityManager = entityManagerFactory.createEntityManager();
            JpaRepositoryFactory repoFactory = new JpaRepositoryFactory(entityManager);
            this.userRepository = repoFactory.getRepository(UserRepository.class);

            this.databaseConnected = true;
            System.out.println("✅ Database connected and JPA initialized!");
        } catch (Exception e) {
            this.databaseConnected = false;
            System.err.println("⚠️ Could not connect to database. Continuing without DB.");
            System.err.println("Exception: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void shutdownDatabase() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
        if (dataSource != null) {
            dataSource.close();
        }
        System.out.println("Closing Data Source, Entity Manager, and Entity Manager Factory");
    }

    public boolean isDatabaseConnected() {
        return databaseConnected;
    }

    public UserRepository getUserRepository() {
        if (userRepository == null)
            throw new IllegalStateException("Database not connected or initialized yet.");
        return userRepository;
    }
}