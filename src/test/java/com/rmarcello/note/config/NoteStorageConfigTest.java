package com.rmarcello.note.config;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import com.rmarcello.note.SpringBootDemoApplication;
import com.rmarcello.note.repository.NoteRepository;
import com.rmarcello.note.service.NoteOperations;
import com.rmarcello.note.service.NoteService;
import com.rmarcello.note.service.NoteServiceDB;

import jakarta.persistence.EntityManagerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class NoteStorageConfigTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(SpringBootDemoApplication.class);

    @Test
    void defaultsToMemoryWithoutDatabaseSettings() {
        contextRunner.run(context -> {
            assertThat(context).hasNotFailed().hasSingleBean(NoteOperations.class);
            assertThat(context.getBean(NoteOperations.class)).isExactlyInstanceOf(NoteService.class);
            assertThat(context).doesNotHaveBean(DataSource.class)
                    .doesNotHaveBean(EntityManagerFactory.class)
                    .doesNotHaveBean(NoteRepository.class);
            assertThat(context).hasBean("init");
        });
    }

    @Test
    void explicitMemoryIgnoresDatabaseSettings() {
        contextRunner.withPropertyValues(
                "notes.storage=memory",
                "spring.datasource.url=jdbc:postgresql://127.0.0.1:1/unavailable",
                "spring.datasource.password=${UNSET_NOTES_TEST_PASSWORD}")
                .run(context -> {
                    assertThat(context).hasNotFailed().hasSingleBean(NoteOperations.class);
                    assertThat(context.getBean(NoteOperations.class)).isExactlyInstanceOf(NoteService.class);
                    assertThat(context).doesNotHaveBean(DataSource.class)
                            .doesNotHaveBean(EntityManagerFactory.class)
                            .doesNotHaveBean(NoteRepository.class);
                });
    }

    @Test
    void selectsDatabaseService() {
        new ApplicationContextRunner()
                .withUserConfiguration(NoteStorageConfig.class)
                .withPropertyValues("notes.storage=postgres")
                .withBean(NoteRepository.class, () -> mock(NoteRepository.class))
                .run(context -> {
                    assertThat(context).hasNotFailed().hasSingleBean(NoteOperations.class);
                    assertThat(context.getBean(NoteOperations.class)).isExactlyInstanceOf(NoteServiceDB.class);
                    assertThat(context).doesNotHaveBean(NoteService.class);
                });
    }

    @Test
    void rejectsUnsupportedStorage() {
        contextRunner.withPropertyValues("notes.storage=mysql").run(context -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure()).hasStackTraceContaining("storage")
                    .hasStackTraceContaining("mysql");
        });
    }

    @Test
    void rejectsEmptyStorage() {
        contextRunner.withPropertyValues("notes.storage=").run(context -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure())
                    .hasStackTraceContaining("notes.storage must be memory or postgres");
        });
    }

    @Test
    @Timeout(20)
    void unavailablePostgresFailsStartupRatherThanFallingBack() {
        contextRunner.withPropertyValues(
                "notes.storage=postgres",
                "spring.datasource.url=jdbc:postgresql://127.0.0.1:1/notes?connectTimeout=1",
                "spring.datasource.username=notes",
                "spring.datasource.password=test",
                "spring.datasource.hikari.connection-timeout=250",
                "spring.jpa.database=postgresql",
                "spring.jpa.hibernate.ddl-auto=validate")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure()).hasStackTraceContaining("127.0.0.1:1");
                });
    }
}
