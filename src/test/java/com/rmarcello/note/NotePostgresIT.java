package com.rmarcello.note;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.rmarcello.note.beans.Note;
import com.rmarcello.note.service.NoteOperations;
import com.rmarcello.note.service.NoteService;
import com.rmarcello.note.service.NoteServiceDB;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled("Temporarily disabled: requires Docker/PostgreSQL")
@SpringBootTest(properties = "notes.storage=postgres")
@AutoConfigureMockMvc
@Testcontainers
class NotePostgresIT extends NoteApiContract {
    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17-alpine")
            .withDatabaseName("notes")
            .withInitScript("db/001-notes.sql");

    @Autowired
    private NoteOperations noteService;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Test
    void preservesCrudAndLabelContract() throws Exception {
        exerciseCrudAndLabelContract();
    }

    @Test
    void loadsTheComposeSamplesAndOnlyTheDatabaseService() {
        assertThat(AopUtils.getTargetClass(noteService)).isEqualTo(NoteServiceDB.class);
        assertThat(applicationContext.getBeansOfType(NoteService.class)).isEmpty();
        assertThat(applicationContext.containsBean("init")).isFalse();
        assertThat(noteService.getAll()).extracting(Note::getId).containsExactly(1L, 2L, 3L, 4L);
        Note meeting = noteService.getById(1);
        assertThat(meeting.getTitle()).isEqualTo("Meeting Notes");
        assertThat(meeting.getContent()).isEqualTo("Discuss project milestones and deadlines.");
        assertThat(meeting.getLabels()).containsExactly("meeting", "project");
        assertThat(meeting.getUrls()).containsExactly(
                "https://example.com/meeting1", "https://example.com/meeting2");
        assertThat(meeting.getColor()).isEqualTo("#FF5733");
        assertThat(noteService.getByLabel("project")).extracting(Note::getId).containsExactly(1L);
    }

    @Test
    void persistsAcrossApplicationRestartsWithoutReseeding() {
        Note saved = noteService.add(new Note(999, "Persistent note", "Survives a new application context",
                List.of("second", "first", "second"),
                List.of("https://example.com/b", "https://example.com/a"), "#AABBCC"));
        assertThat(saved.getId()).isGreaterThan(4).isNotEqualTo(999);
        int id = Math.toIntExact(saved.getId());
        try (ConfigurableApplicationContext restarted = new SpringApplicationBuilder(SpringBootDemoApplication.class)
                .web(WebApplicationType.NONE)
                .run("--notes.storage=postgres",
                        "--spring.datasource.url=" + POSTGRES.getJdbcUrl(),
                        "--spring.datasource.username=" + POSTGRES.getUsername(),
                        "--spring.datasource.password=" + POSTGRES.getPassword())) {
            NoteOperations restartedService = restarted.getBean(NoteOperations.class);
            assertThat(restartedService.getAll()).hasSize(5);
            Note persisted = restartedService.getById(id);
            assertThat(persisted.getTitle()).isEqualTo(saved.getTitle());
            assertThat(persisted.getContent()).isEqualTo(saved.getContent());
            assertThat(persisted.getLabels()).containsExactly("second", "first", "second");
            assertThat(persisted.getUrls()).containsExactly(
                    "https://example.com/b", "https://example.com/a");
            assertThat(persisted.getColor()).isEqualTo("#AABBCC");
            assertThat(restartedService.getByLabel("second")).extracting(Note::getId).containsExactly(saved.getId());

            persisted.setTitle("Updated after restart");
            restartedService.update(id, persisted);
            assertThat(noteService.getById(id).getTitle()).isEqualTo("Updated after restart");
        } finally {
            noteService.remove(id);
        }
        assertThat(noteService.getById(id)).isNull();
        assertThat(noteService.getByLabel("second")).isEmpty();
    }
}
