package com.api.materiais.repository;

import com.api.materiais.model.CotacaoLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CotacaoLogRepository extends JpaRepository<CotacaoLog, Long> {
    List<CotacaoLog> findByTenantIdOrderByDataHoraCotacaoDesc(String tenantId);
}
