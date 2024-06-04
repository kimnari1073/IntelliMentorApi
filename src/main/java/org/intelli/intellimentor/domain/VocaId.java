package org.intelli.intellimentor.domain;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class VocaId implements Serializable {


    private String eng;
    private Long vocaListId; // VocaList의 기본 키를 저장
}
