package com.example.demo.configuration;

import com.example.demo.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;
import java.util.Properties;

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
    private UserRepository userRepository;
    private boolean databaseConnected = false;

    // Run right after bean is constructed
    @PostConstruct
    public void initializeDatabase() {
        try {
            System.out.println("🔄 Attempting to connect to database...");

            DataSource dataSource = DataSourceBuilder.create()
                    .driverClassName(dbDriver)
                    .url(dbUrl)
                    .username(dbUsername)
                    .password(dbPassword)
                    .build();

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

            EntityManager em = entityManagerFactory.createEntityManager();
            JpaRepositoryFactory repoFactory = new JpaRepositoryFactory(em);
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

    public boolean isDatabaseConnected() {
        return databaseConnected;
    }

    public UserRepository getUserRepository() {
        if (userRepository == null)
            throw new IllegalStateException("Database not connected or initialized yet.");
        return userRepository;
    }
}

//@Configuration
//public class DatabaseInitializer {
//
//    @Value("${spring.datasource.url}")
//    private String dbUrl;
//    @Value("${spring.datasource.username}")
//    private String dbUsername;
//    @Value("${spring.datasource.password}")
//    private String dbPassword;
//    @Value("${spring.datasource.driver-class-name}")
//    private String dbDriver;
//
//    @Bean
//    public UserRepository userRepository() {
//        try {
//            DataSource dataSource = DataSourceBuilder.create()
//                    .driverClassName(dbDriver)
//                    .url(dbUrl)
//                    .username(dbUsername)
//                    .password(dbPassword)
//                    .build();
//
//            LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
//            factoryBean.setDataSource(dataSource);
//            factoryBean.setPackagesToScan("com.example.demo.models");
//            factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
//
//            Properties props = new Properties();
//            props.put("hibernate.hbm2ddl.auto", "update");
//            props.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
//            factoryBean.setJpaProperties(props);
//
//            factoryBean.afterPropertiesSet();
//            EntityManagerFactory emf = factoryBean.getObject();
//            EntityManager em = emf.createEntityManager();
//            JpaRepositoryFactory repoFactory = new JpaRepositoryFactory(em);
//
//            System.out.println("✅ Database connected, UserRepository bean created!");
//            return repoFactory.getRepository(UserRepository.class);
//
//        } catch (Exception e) {
//            System.err.println("⚠️ Could not connect to database. UserRepository bean NOT created.");
//            return null; // Spring will ignore null beans
//        }
//    }
//}


