package com.ctfera.java_spb.service;

import com.ctfera.java_spb.dto.PropostaRequestDTO;
import com.ctfera.java_spb.dto.PropostaResponseDTO;
import com.ctfera.java_spb.entity.Proposta;
import com.ctfera.java_spb.repository.PropostaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service //Anotação de camada service para definir a responsabilidade de gerência do Spring
public class PropostaService {

    private PropostaRepository propostaRepository;

    public PropostaResponseDTO criar (PropostaRequestDTO requestDTO){
        propostaRepository.save(new Proposta());
        return null;
    }
}
