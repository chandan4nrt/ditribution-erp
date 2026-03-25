package com.ecomm.nrt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecomm.nrt.entity.Menu;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

	List<Menu> findByParentIsNullOrderBySequenceAsc();
	
	List<Menu> findByRoleCodeAndParentIsNullOrderBySequenceAsc(String roleCode);
}
