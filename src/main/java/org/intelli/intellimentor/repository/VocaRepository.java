package org.intelli.intellimentor.repository;

import org.intelli.intellimentor.domain.Voca;
import org.intelli.intellimentor.dto.VocaListDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface VocaRepository extends JpaRepository<Voca,Long> {
    @Query("select v.title,COUNT(v) from Voca v WHERE v.userId = :userId GROUP BY v.title")
    List<Object[]> getVocaCount(@Param("userId")String userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Voca v WHERE v.userId = :userId AND v.title = :title")
    void deleteByUserIdAndTitle(@Param("userId") String userId, @Param("title") String title);
}
