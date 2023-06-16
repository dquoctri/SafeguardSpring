/*
 * Copyright (c) 2023 Deadline
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.safeguard.repository;

import com.dqtri.mango.safeguard.model.SafeguardUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<SafeguardUser, Long> {
    Optional<SafeguardUser> findByEmail(String email);

    boolean existsByEmail(String email);
}
