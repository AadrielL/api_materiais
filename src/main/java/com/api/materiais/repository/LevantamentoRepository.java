package com.api.materiais.repository;

import com.api.materiais.model.Levantamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LevantamentoRepository extends JpaRepository<Levantamento, Long> {
    // Busca e ordena pelo ID (que no banco costuma seguir a ordem de inserção)
    List<Levantamento> findByOrcamentoIdOrderByIdAsc(String orcamentoId);
}