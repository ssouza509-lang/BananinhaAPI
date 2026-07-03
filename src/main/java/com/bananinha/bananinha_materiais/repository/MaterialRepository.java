package com.bananinha.bananinha_materiais.repository;

import com.bananinha.bananinha_materiais.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long>, JpaSpecificationExecutor<Material> {
}
