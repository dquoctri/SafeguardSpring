package com.dqtri.mango.safeguard.repository;

import com.dqtri.mango.safeguard.model.BlackListRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackListRefreshTokenRepository extends JpaRepository<BlackListRefreshToken, Long> {
    boolean existsByToken(String token);
}

