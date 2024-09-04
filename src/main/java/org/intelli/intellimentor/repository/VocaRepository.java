package org.intelli.intellimentor.repository;

import jakarta.transaction.Transactional;
import org.intelli.intellimentor.domain.Section;
import org.intelli.intellimentor.domain.Voca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VocaRepository extends JpaRepository<Voca,Long> {
    //유저 단어 리스트 조회
    @Query("SELECT v.title.id, v.title.title, COUNT(v), COALESCE(MAX(s.section), 0) " +
            "FROM Voca v " +
            "JOIN v.title t " +
            "LEFT JOIN v.section s " +
            "WHERE v.userId = :userId " +
            "GROUP BY v.title.id, v.title.title " +
            "ORDER BY v.title.id")
    List<Object[]> getVocaList(@Param("userId")String userId);

    //유저 단어 데이터 조회
    @Query("SELECT v FROM Voca v " +
            "WHERE v.title.id = :titleId " +
            "ORDER BY v.id")
    List<Voca> getVocaListDetails(@Param("titleId") Long titleId);

    //section 조회
    @Query("SELECT DISTINCT v.section.id " +
            "FROM Voca v " +
            "WHERE v.title.id=:titleId")
    List<Long> getSectionList(@Param("titleId")Long titleId);

    // 유저 단어 데이터 조회 (특정 섹션)
//    List<Voca> findByUserIdAndTitleAndSection(@Param("userId") String userId, @Param("title") String title, @Param("section") int section);

    //섹션별 단어 리스트
//    @Query("SELECT v FROM Voca v " +
//            "WHERE v.userId = :userId AND v.title = :title " +
//            "ORDER BY v.section ASC, v.bookmark DESC")
//    List<Voca> findVocaOrderBySection(@Param("userId") String userId, @Param("title") String title);

//    @Transactional
//    @Modifying
//    @Query("DELETE FROM Voca v " +
//            "WHERE v.userId = :userId AND v.title = :title")
//    void deleteVocaList(@Param("userId") String userId, @Param("title") String title);

//    @Modifying
//    @Transactional
//    @Query("UPDATE Voca v " +
//            "SET v.section = 0, v.bookmark = false, v.mistakes = 0 " +
//            "WHERE v.userId = :userId AND v.title = :title")
//    void deleteLearn(@Param("userId") String userId, @Param("title") String title);


}
