package org.itstep.projectdeadlinemanagement.repository;

import org.itstep.projectdeadlinemanagement.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {
}
