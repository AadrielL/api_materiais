package com.api.materiais.repository;

import com.api.materiais.model.Levantamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LevantamentoRepository extends JpaRepository<Levantamento, Long> {
    // Busca todos os levantamentos de um eletricista específico
    List<Levantamento> findByTenantId(String tenantId);
}