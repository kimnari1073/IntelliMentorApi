package org.intelli.intellimentor.repository;

import jakarta.transaction.Transactional;
import org.intelli.intellimentor.domain.Section;
import org.intelli.intellimentor.domain.Voca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VocaRepository extends JpaRepository<Voca,Long> {
    //단어 데이터 조회(전체)
    List<Voca> findByTitleIdOrderById(Long titleId);

    //단어 데이터 조회(한 개)
    Voca findFirstByTitleId(Long titleId);

    //단어 데이터 조회(섹션별)
    List<Voca> findBySectionIdOrderById(Long sectionId);



    //유저 단어 리스트 조회
    @Query("SELECT v.title.id, v.title.title, COUNT(v), COALESCE(MAX(s.section), 0) " +
            "FROM Voca v " +
            "JOIN v.title t " +
            "LEFT JOIN v.section s " +
            "WHERE v.userId = :userId " +
            "GROUP BY v.title.id, v.title.title " +
            "ORDER BY v.title.id")
    List<Object[]> getVocaList(@Param("userId")String userId);



    //section 조회
    @Query("SELECT DISTINCT v.section.id " +
            "FROM Voca v " +
            "WHERE v.title.id=:titleId")
    List<Long> getSectionList(@Param("titleId")Long titleId);

    @Query("SELECT v FROM Voca v WHERE v.section.id = :sectionId")
    List<Voca> getVocaBySectionId(@Param("sectionId")Long sectionId);



}
