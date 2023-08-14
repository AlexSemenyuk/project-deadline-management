package org.itstep.projectdeadlinemanagement.repository;

import org.itstep.projectdeadlinemanagement.model.Assembly;
import org.itstep.projectdeadlinemanagement.model.PartList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartListRepository extends JpaRepository<PartList, Integer>{
}
