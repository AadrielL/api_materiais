package com.api.materiais.repository;

import com.api.materiais.model.Levantamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LevantamentoRepository extends JpaRepository<Levantamento, Long> {
    List<Levantamento> findByTenantId(String tenantId); // Busca isolada por dono
}