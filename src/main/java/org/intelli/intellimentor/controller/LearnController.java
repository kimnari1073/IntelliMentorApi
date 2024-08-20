package org.intelli.intellimentor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/learn")
public class LearnController {

    //조회: 단어장제목,영어,한글,섹션 {[섹션:1,단어:[eng,kor]],[섹션,단어]}]
    //List<LearnDTO>
    //{
    // [section:1,
    //  word:[{apple:사과},{banana:바나나}]
    //  ],
    // [section:2,
    //  word:[{...},{...}]
    // ]
    //}

    //섹션 나누기
}
