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

    // 유저 단어 데이터 조회
    List<Voca> findByUserIdAndTitle(@Param("userId") String userId, @Param("title") String title);
    // 유저 단어 데이터 조회 (특정 섹션)
    List<Voca> findByUserIdAndTitleAndSection(@Param("userId") String userId, @Param("title") String title, @Param("section") int section);

    //섹션별 단어 리스트
    @Query("SELECT v FROM Voca v " +
            "WHERE v.userId = :userId AND v.title = :title " +
            "ORDER BY v.section ASC, v.bookmark DESC")
    List<Voca> findVocaOrderBySection(@Param("userId") String userId, @Param("title") String title);

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
