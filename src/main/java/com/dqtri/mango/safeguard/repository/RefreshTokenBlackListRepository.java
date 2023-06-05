package com.dqtri.mango.safeguard.repository;

import com.dqtri.mango.safeguard.model.RefreshTokenBlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenBlackListRepository extends JpaRepository<RefreshTokenBlackList, Long> {
    boolean existsByToken(String token);
}

