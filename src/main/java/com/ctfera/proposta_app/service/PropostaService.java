package com.ctfera.proposta_app.service;

import com.ctfera.proposta_app.dto.PropostaRequestDTO;
import com.ctfera.proposta_app.dto.PropostaResponseDTO;
import com.ctfera.proposta_app.entity.Proposta;
import com.ctfera.proposta_app.mapper.PropostaMapper;
import com.ctfera.proposta_app.repository.PropostaRepository;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

//@AllArgsConstructor //Anotação Spring que usa o Lombok para fazer a injeção de dependências. //Commentado e substituído por construtor
@Service //Anotação de camada service para definir a responsabilidade de gerência do Spring
public class PropostaService {

    private PropostaRepository propostaRepository;

    private NotificacaoRabbitService notificacaoRabbitService;

    //Por causa de ser um valor de propriedade, não será usado um Bean, mas sim o construtor.
    private String exchange;

    public PropostaService(PropostaRepository propostaRepository,
                           NotificacaoRabbitService notificacaoRabbitService,
                           @Value("${spring.rabbitmq.propostapendente.exchange}")String exchange) {

        this.propostaRepository = propostaRepository;
        this.notificacaoRabbitService = notificacaoRabbitService;
        this.exchange = exchange;

    }

    public PropostaResponseDTO criar (PropostaRequestDTO requestDTO){
        Proposta proposta =  PropostaMapper.INSTANCE.convertDtoToProposta(requestDTO);
        propostaRepository.save(proposta);

        //Definindo a prioridade de leitura da proposta na fila rabbit
        int prioridade = proposta.getUsuario().getRenda() > 10000 ? 10 : 5;

        MessagePostProcessor messagePostProcessor = message -> {
            message.getMessageProperties().setPriority(prioridade);
            return message;
        };

        // PropostaResponseDTO response = PropostaMapper.INSTANCE.convertEntityToDto(proposta);
        // notificacaoService.notificar(response, exchange);

        notificarRabbitMQ(proposta, messagePostProcessor);

        return PropostaMapper.INSTANCE.convertEntityToDto(proposta);
    }

    //Método de notificação com tratamento para casos de falha, onde salva false para integração concluída
    private void notificarRabbitMQ(Proposta proposta, MessagePostProcessor messagePostProcessor){
       try{
            notificacaoRabbitService.notificar(proposta, exchange, messagePostProcessor);
       }catch(RuntimeException ex){
           propostaRepository.atualizarProposta(proposta.getId(), false, "");
       }
    }



    public List<PropostaResponseDTO> obterProposta() {
        return PropostaMapper.INSTANCE.convertListEntityToListDto( propostaRepository.findAll() );
    }
}
