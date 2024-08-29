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
    @Query("select v.title,COUNT(v),MAX(v.section) " +
            "from Voca v " +
            "WHERE v.userId = :userId GROUP BY v.title")
    List<Object[]> getVocaList(@Param("userId")String userId);
    List<Voca> findByUserIdAndTitle(@Param("userId") String userId, @Param("title") String title);// 특정 userId와 title을 기준으로 데이터 조회

    @Query("SELECT v.section, v.eng, v.kor, v.bookmark, v.mistakes " +
            "FROM Voca v " +
            "WHERE v.userId = :userId AND v.title = :title " +
            "GROUP BY v.section, v.eng, v.kor, v.bookmark, v.mistakes")
    List<Object[]> findDataGroupBySection(@Param("userId") String userId, @Param("title") String title);

    @Transactional
    @Modifying
    @Query("DELETE FROM Voca v " +
            "WHERE v.userId = :userId AND v.title = :title")
    void deleteVocaList(@Param("userId") String userId, @Param("title") String title);

    @Modifying
    @Transactional
    @Query("UPDATE Voca v " +
            "SET v.section = 0, v.bookmark = false, v.mistakes = 0 " +
            "WHERE v.userId = :userId AND v.title = :title")
    void deleteLearn(@Param("userId") String userId, @Param("title") String title);


}
