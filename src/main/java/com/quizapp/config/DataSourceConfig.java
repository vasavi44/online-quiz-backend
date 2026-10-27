package com.quizapp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${spring.datasource.url:}")
    private String rawUrl;

    @Value("${spring.datasource.username:sa}")
    private String defaultUsername;

    @Value("${spring.datasource.password:}")
    private String defaultPassword;

    @Value("${spring.datasource.driver-class-name:}")
    private String driverClassName;

    @Bean
    @Primary
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl != null && !databaseUrl.isBlank() &&
                (databaseUrl.startsWith("postgres://") || databaseUrl.startsWith("postgresql://"))) {
            try {
                log.info("Detected cloud DATABASE_URL. Parsing connection details...");
                URI uri = new URI(databaseUrl);
                String userInfo = uri.getUserInfo();
                String username = defaultUsername;
                String password = defaultPassword;
                if (userInfo != null && userInfo.contains(":")) {
                    String[] parts = userInfo.split(":", 2);
                    username = parts[0];
                    password = parts[1];
                }
                int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                String path = uri.getPath();
                String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + port + path;

                log.info("Successfully configured PostgreSQL JDBC connection for: {}:{}", uri.getHost(), port);
                return DataSourceBuilder.create()
                        .url(jdbcUrl)
                        .username(username)
                        .password(password)
                        .driverClassName("org.postgresql.Driver")
                        .build();
            } catch (Exception e) {
                log.error("Failed to parse DATABASE_URL: {}. Falling back to standard configuration.", e.getMessage());
            }
        }

        // Standard configuration or fallback
        String finalUrl = (rawUrl != null && !rawUrl.isBlank()) ? rawUrl :
                "jdbc:h2:./data/quizdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1";

        String driver = (driverClassName != null && !driverClassName.isBlank()) ? driverClassName :
                (finalUrl.contains("postgresql") ? "org.postgresql.Driver" : "org.h2.Driver");

        log.info("Using DataSource URL: {}", finalUrl);
        return DataSourceBuilder.create()
                .url(finalUrl)
                .username(defaultUsername)
                .password(defaultPassword)
                .driverClassName(driver)
                .build();
    }
}
