package com.rmarcello.note.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rmarcello.note.model.NoteEntity;

public interface NoteRepository extends JpaRepository<NoteEntity, Long> {
    List<NoteEntity> findAllByOrderByIdAsc();

    @Query("select n from NoteEntity n where :label member of n.labels order by n.id")
    List<NoteEntity> findByLabel(@Param("label") String label);
}
