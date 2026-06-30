package com.redshanflora.redshanflora_backend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Component to synchronize the PostgreSQL database auto-increment sequences with the
 * actual maximum IDs present in the tables on application startup.
 * This prevents primary key duplicate constraint errors (e.g. key already exists)
 * that commonly occur after manual inserts, DB restores, or synchronization mismatches.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSequenceSync implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        log.info("Starting database sequence synchronization...");
        try {
            // Find and reset the sequence of the "user" table to MAX(user_id) + 1
            // Use pg_get_serial_sequence to dynamically resolve the sequence name
            String sql = "SELECT setval(pg_get_serial_sequence('\"user\"', 'user_id'), COALESCE((SELECT MAX(user_id) FROM \"user\"), 0) + 1, false)";
            jdbcTemplate.execute(sql);
            log.info("PostgreSQL sequence for 'user' table has been synchronized successfully.");
        } catch (Exception e) {
            log.error("Failed to synchronize PostgreSQL sequence for 'user' table", e);
        }
    }
}
