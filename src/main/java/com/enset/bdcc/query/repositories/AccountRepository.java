package com.enset.bdcc.query.repositories;

import com.enset.bdcc.query.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
}