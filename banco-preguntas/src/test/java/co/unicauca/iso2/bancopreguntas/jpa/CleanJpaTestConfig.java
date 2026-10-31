/**
 * @file CleanJpaTestConfig.java
 * @brief Spring configuration for H2 in-memory testing of clean JPA persistence (Taller 10).
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.jpa;

import co.unicauca.iso2.bancopreguntas.jpa.domain.repository.CleanQuestionRepository;
import co.unicauca.iso2.bancopreguntas.jpa.domain.repository.CleanQuizRepository;
import co.unicauca.iso2.bancopreguntas.jpa.domain.service.CleanQuizService;
import co.unicauca.iso2.bancopreguntas.jpa.infrastructure.adapter.CleanQuestionRepositoryJpaAdapter;
import co.unicauca.iso2.bancopreguntas.jpa.infrastructure.adapter.CleanQuizRepositoryJpaAdapter;
import co.unicauca.iso2.bancopreguntas.jpa.infrastructure.mapper.CleanDomainMapper;
import co.unicauca.iso2.bancopreguntas.jpa.infrastructure.springdata.SpringQuestionRecordRepository;
import co.unicauca.iso2.bancopreguntas.jpa.infrastructure.springdata.SpringQuizRecordRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@TestConfiguration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "co.unicauca.iso2.bancopreguntas.jpa.infrastructure.springdata")
@EntityScan(basePackages = "co.unicauca.iso2.bancopreguntas.jpa.infrastructure.record")
public class CleanJpaTestConfig {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl("jdbc:h2:mem:clean_jpa_test_db;DB_CLOSE_DELAY=-1");
        ds.setUsername("sa");
        ds.setPassword("");
        return ds;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("co.unicauca.iso2.bancopreguntas.jpa.infrastructure.record");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(false);
        em.setJpaVendorAdapter(vendorAdapter);

        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        em.setJpaProperties(properties);

        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return transactionManager;
    }

    @Bean
    public CleanDomainMapper cleanDomainMapper() {
        return new CleanDomainMapper();
    }

    @Bean
    public CleanQuestionRepository cleanQuestionRepository(SpringQuestionRecordRepository springDataRepo, CleanDomainMapper mapper) {
        return new CleanQuestionRepositoryJpaAdapter(springDataRepo, mapper);
    }

    @Bean
    public CleanQuizRepository cleanQuizRepository(SpringQuizRecordRepository springDataQuizRepo, CleanDomainMapper mapper) {
        return new CleanQuizRepositoryJpaAdapter(springDataQuizRepo, mapper);
    }

    @Bean
    public CleanQuizService cleanQuizService(CleanQuestionRepository questionRepo, CleanQuizRepository quizRepo) {
        return new CleanQuizService(questionRepo, quizRepo);
    }
}

