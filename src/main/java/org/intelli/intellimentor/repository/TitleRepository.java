package org.intelli.intellimentor.repository;

import org.intelli.intellimentor.domain.Title;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TitleRepository extends JpaRepository<Title, Long> {
    @Query("SELECT t.title " +
            "FROM Title t " +
            "WHERE t.id = :id")
    String getTitle(@Param("id") Long id);

}
