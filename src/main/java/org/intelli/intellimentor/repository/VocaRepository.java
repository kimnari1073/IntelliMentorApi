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
    @Query("select v.title,COUNT(v),MAX(v.section) from Voca v WHERE v.userId = :userId GROUP BY v.title")
    List<Object[]> getVocaList(@Param("userId")String userId);

    // 특정 userId와 title을 기준으로 데이터 조회
    List<Voca> findByUserIdAndTitle(@Param("userId") String userId, @Param("title") String title);

    @Query("SELECT v.section, v.eng, v.kor FROM Voca v WHERE v.userId = :userId AND v.title=:title GROUP BY v.section, v.eng, v.kor")
    List<Object[]> findWordsGroupedBySection(@Param("userId") String userId,@Param("title")String title);

    @Transactional
    @Modifying
    @Query("DELETE FROM Voca v WHERE v.userId = :userId AND v.title = :title")
    void deleteByUserIdAndTitle(@Param("userId") String userId, @Param("title") String title);
}
