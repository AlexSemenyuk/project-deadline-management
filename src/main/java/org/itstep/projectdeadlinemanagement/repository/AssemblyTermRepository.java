package org.itstep.projectdeadlinemanagement.repository;

import org.itstep.projectdeadlinemanagement.model.TechnologyAssembly;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssemblyTermRepository extends JpaRepository<TechnologyAssembly, Integer> {
}
