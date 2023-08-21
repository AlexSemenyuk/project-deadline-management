package org.itstep.projectdeadlinemanagement.repository;

import org.itstep.projectdeadlinemanagement.model.ProductionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductionPlanRepository extends JpaRepository<ProductionPlan, Integer> {
    // List<Part> findAllByNameNot(String name);
    // List<Part> findAllByName(String name);
    @Query("SELECT p FROM ProductionPlan p WHERE YEAR(p.currentStart) = :year")
    List<ProductionPlan> findByCurrentStartYear(int year);
}
