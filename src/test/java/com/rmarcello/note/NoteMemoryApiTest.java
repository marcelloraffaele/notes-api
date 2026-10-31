package com.rmarcello.note;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "notes.storage=memory")
@AutoConfigureMockMvc
class NoteMemoryApiTest extends NoteApiContract {
    @Test
    void preservesCrudAndLabelContract() throws Exception {
        exerciseCrudAndLabelContract();
    }
}
