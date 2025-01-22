package com.ctfera.proposta_app.service;

import com.ctfera.proposta_app.dto.PropostaResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    @Autowired //Necessita da anotação "@EnableWebSocketMessageBroker" na class de configuração do WebSocket ou na Main, para realizar a injeção de dependência.
    private SimpMessagingTemplate template;

    public void notificar(PropostaResponseDTO proposta){
        template.convertAndSend("/propostas", proposta);
    }
}
