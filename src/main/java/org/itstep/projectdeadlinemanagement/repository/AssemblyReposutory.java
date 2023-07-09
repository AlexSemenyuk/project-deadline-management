package org.itstep.projectdeadlinemanagement.repository;

import org.itstep.projectdeadlinemanagement.model.Assembly;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssemblyReposutory extends JpaRepository<Assembly, Integer> {
}
