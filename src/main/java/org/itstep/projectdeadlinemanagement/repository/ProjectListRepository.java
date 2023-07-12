package org.itstep.projectdeadlinemanagement.repository;

import org.itstep.projectdeadlinemanagement.model.ProjectList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectListRepository extends JpaRepository<ProjectList, Integer> {
}
