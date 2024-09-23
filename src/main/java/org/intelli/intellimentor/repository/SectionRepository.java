package org.intelli.intellimentor.repository;

import org.intelli.intellimentor.domain.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section,Long> {

    @Query("SELECT s.grade FROM Section s WHERE s.id IN :sectionIds")
    List<String> findGradesBySectionIds(@Param("sectionIds") List<Long> sectionIds);
}
