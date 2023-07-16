package org.itstep.projectdeadlinemanagement.repository;

import org.itstep.projectdeadlinemanagement.model.TaskCondition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskConditionRepository extends JpaRepository<TaskCondition, Integer> {
}
