package com.ctfera.proposta_app.listener;

import com.ctfera.proposta_app.entity.Proposta;
import com.ctfera.proposta_app.repository.PropostaRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PropostaConcluidaListener {

    @Autowired
    private PropostaRepository propostaRepository;

    @RabbitListener(queues = "${rabbitmq.queue.proposta.concluida}")
    public void propostaConcluida(Proposta proposta){
        propostaRepository.save(proposta);
    }


}
