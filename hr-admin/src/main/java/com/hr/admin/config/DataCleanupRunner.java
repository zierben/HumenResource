package com.hr.admin.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DataCleanupRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataCleanupRunner.class);
    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    public DataCleanupRunner(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) {
        try {
            log.info("Reinitializing organization structure...");
            jdbcTemplate.update("DELETE FROM sys_user WHERE id > 1");
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("init_org_structure.sql"));
            populator.execute(dataSource);
            log.info("Organization structure initialized successfully");
        } catch (Exception e) {
            log.warn("Data initialization skipped: {}", e.getMessage());
        }
    }
}
