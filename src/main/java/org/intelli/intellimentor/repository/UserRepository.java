package org.intelli.intellimentor.repository;

import org.intelli.intellimentor.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
