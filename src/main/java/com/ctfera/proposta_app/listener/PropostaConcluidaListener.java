package com.ctfera.proposta_app.listener;

import com.ctfera.proposta_app.dto.PropostaResponseDTO;
import com.ctfera.proposta_app.entity.Proposta;
import com.ctfera.proposta_app.mapper.PropostaMapper;
import com.ctfera.proposta_app.repository.PropostaRepository;
import com.ctfera.proposta_app.service.WebSocketService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PropostaConcluidaListener {

    @Autowired
    private PropostaRepository propostaRepository;

    @Autowired
    private WebSocketService webSocketService;

    @RabbitListener(queues = "${rabbitmq.queue.proposta.concluida}")
    public void propostaConcluida(Proposta proposta){
        propostaRepository.save(proposta);
        PropostaResponseDTO responseDTO = PropostaMapper.INSTANCE.convertEntityToDto(proposta);
        webSocketService.notificar(responseDTO);
    }

    private void atualizarProposta(Proposta proposta){
        propostaRepository.atualizarProposta(proposta.getId(), proposta.getAprovada(), proposta.getObservacao());
    }


}
