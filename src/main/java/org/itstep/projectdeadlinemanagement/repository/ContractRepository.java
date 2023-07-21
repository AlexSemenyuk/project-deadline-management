package org.itstep.projectdeadlinemanagement.repository;

import org.itstep.projectdeadlinemanagement.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Integer> {

}
