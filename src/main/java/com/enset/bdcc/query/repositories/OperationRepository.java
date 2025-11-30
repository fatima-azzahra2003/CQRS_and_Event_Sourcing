package com.enset.bdcc.query.repositories;


import com.enset.bdcc.query.entities.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationRepository extends JpaRepository<Operation, Long> {
}
