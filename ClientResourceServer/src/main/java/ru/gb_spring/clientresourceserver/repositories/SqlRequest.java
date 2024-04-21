package ru.gb_spring.clientresourceserver.repositories;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

/**
 * Шаблон SQL запросов
 */
@Configuration
@ConfigurationProperties(prefix = "sqlrequest")
@ConfigurationPropertiesScan
@Setter
@Getter
public class SqlRequest {
    private String findAll;
    private String save;
    private String delete;
    private String getById;
    private String update;
    private String count;
}
