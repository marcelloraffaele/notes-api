package com.rmarcello.note.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.rmarcello.note.model.NoteEntity;
import com.rmarcello.note.repository.NoteRepository;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "notes.storage", havingValue = "postgres")
@Import({DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, TransactionAutoConfiguration.class})
@EntityScan(basePackageClasses = NoteEntity.class)
@EnableJpaRepositories(basePackageClasses = NoteRepository.class)
public class PostgresConfig {
}
