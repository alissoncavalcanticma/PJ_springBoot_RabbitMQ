package com.ctfera.proposta_app.repository;

import com.ctfera.proposta_app.entity.Proposta;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropostaRepository extends CrudRepository<Proposta, Long> {

    //uso de query derivada, onde o Spring entende a necessidade e abstrai uma consulta "SELECT * FROM tb_proposta WHERE integrada = 'false'"
    List<Proposta> findAllByIntegradaIsFalse();

}
