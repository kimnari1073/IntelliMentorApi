package org.intelli.intellimentor.repository;

import org.intelli.intellimentor.domain.VocaList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VocaRepository extends JpaRepository<VocaList,Long> {
}
