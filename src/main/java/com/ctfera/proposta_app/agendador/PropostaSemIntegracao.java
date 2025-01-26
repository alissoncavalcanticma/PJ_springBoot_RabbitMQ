package com.ctfera.proposta_app.agendador;

//Classe para buscar no banco de dados as propostas sem integração e enviar novamente para a fila

import com.ctfera.proposta_app.entity.Proposta;
import com.ctfera.proposta_app.repository.PropostaRepository;
import com.ctfera.proposta_app.service.NotificacaoRabbitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class PropostaSemIntegracao {

    private final PropostaRepository propostaRepository;

    private final NotificacaoRabbitService notificacaoRabbitService;

    private final String exchange;

    //Utilizando lib  org.slf4j
    public final Logger logger = LoggerFactory.getLogger(PropostaSemIntegracao.class);

    public PropostaSemIntegracao(
            PropostaRepository propostaRepository,
            NotificacaoRabbitService notificacaoRabbitService,
            @Value("${spring.rabbitmq.propostapendente.exchange}") String exchange
    ){
        this.propostaRepository = propostaRepository;
        this.notificacaoRabbitService = notificacaoRabbitService;
        this.exchange = exchange;
    }

    //Annotation para Bean de agendamento com parâmetros de frequência de execução
    //Annotation @EnableScheduling deve ser acrescentado ao método main
    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void buscarPropostasSemIntegracao(){
        logger.info("0 - Verificação de proposta sem integração..."); //Gerar Log de início de execução.
        propostaRepository.findAllByIntegradaIsFalse().forEach(proposta -> {
           try{
               logger.info("*** - Existe proposta pendente de integração! - ***"); //Gerar Log de execução do Job
               notificacaoRabbitService.notificar(proposta, exchange);
               logger.info("1 - Proposta está sendo integrada..."); //Gerar Log de execução do Job
               atualizarProposta(proposta);
               logger.info("2 - Proposta pendente integrada com sucesso!"); //Gerar Log de execução do Job
               logger.info("Execução de JOB Finalizada."); //Gerar Log de fim de execução.
           }catch(RuntimeException ex){
                logger.error(ex.getMessage());
           }
        });
        logger.info("*** - Não existem propostas não integradas! - ***");
    }

    private void atualizarProposta(Proposta proposta){
        proposta.setIntegrada(true);
        propostaRepository.save(proposta);
    }

}
