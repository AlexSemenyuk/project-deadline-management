package org.itstep.projectdeadlinemanagement.repository;

import org.itstep.projectdeadlinemanagement.model.Division;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DivisionRepository extends JpaRepository<Division, Integer> {
}
