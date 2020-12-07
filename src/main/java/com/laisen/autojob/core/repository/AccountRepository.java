package com.laisen.autojob.core.repository;

import com.laisen.autojob.core.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findAllByUserIdAndType(String userId, String type);
}
