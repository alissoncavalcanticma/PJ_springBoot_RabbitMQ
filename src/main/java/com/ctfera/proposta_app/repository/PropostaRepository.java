package com.ctfera.proposta_app.repository;

import com.ctfera.proposta_app.entity.Proposta;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PropostaRepository extends CrudRepository<Proposta, Long> {

    //uso de query derivada, onde o Spring entende a necessidade e abstrai uma consulta "SELECT * FROM tb_proposta WHERE integrada = 'false'"
    List<Proposta> findAllByIntegradaIsFalse();

    //Declaração de método de UPDATE usando nativeQuery
    @Transactional
    @Modifying
    @Query(value = "UPDATE proposta SET aprovada = :aprovada, observacao = :observacao WHERE id = :id", nativeQuery = true)
    void atualizarProposta(Long id, boolean aprovada, String observacao);

}
