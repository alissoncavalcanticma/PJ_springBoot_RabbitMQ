package com.ctfera.proposta_app.config;

//import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfiguration {

        //Get values of properties

            //Exchanges
        @Value("${spring.rabbitmq.propostapendente.exchange}") //passando valor de property
        private String exchangePropostaPendente;

        @Value("${spring.rabbitmq.propostaconcluida.exchange}") //passando valor de property
        private String exchangePropostaConcluida;

        @Value("${spring.rabbitmq.propostapendente-dlq.exchange}")
        private String exchangePropostaPendenteDLQ;


            //Queues
        @Value("${spring.rabbitmq.queue.proposta-pendente}")
        private String queuePropostaPendenteToAnaliseCredito;

        @Value("${spring.rabbitmq.queue.proposta-pendente-notificacao}")
        private String queuePropostaPendenteToNotificacao;

        @Value("${spring.rabbitmq.queue.proposta-concluida}")
        private String queuePropostaConcluida;

        @Value("${spring.rabbitmq.queue.proposta-concluida-notificacao}")
        private String queuePropostaConcluidaToNotificacao;

        @Value("${spring.rabbitmq.queue.proposta-pendente-dlq}")
        private String queuePropostaPendenteDLQ;



        //Create Exchanges

        //"proposta-pendente"
        //Setando @Bean para entrega do response do método à gerência do Spring.

        @Bean
        public FanoutExchange criarFanoutExchangePropostaPendente(){
            return ExchangeBuilder.fanoutExchange(exchangePropostaPendente).build();
        }

        //"proposta-concluida"

        @Bean
        public FanoutExchange criarFanoutExchangePropostaConcluida(){
            return ExchangeBuilder.fanoutExchange(exchangePropostaConcluida).build();
        }

        //Exchange DLQ
        @Bean
        public FanoutExchange deadLetterExchange(){
            return ExchangeBuilder.fanoutExchange(exchangePropostaPendenteDLQ).build();
        }


        //Create Queues

        //Filas foram criadas nesse microserviço, mas deveriam ser criadas de acordo com a responsabilidade de cada microserviço.
        //@Bean é utilizado quando se quer passar a instância de response do método para a gerência do Spring
        //Obs: @Bean não passa o método, e sim o response dele para o Spring


        // - Queue proposta pendente
        @Bean
        public Queue criarFilaPropostaPendenteMsAnaliseCredito(){
            return QueueBuilder.durable(queuePropostaPendenteToAnaliseCredito)
                    .maxLength(2L)
                    //.ttl(10000) -> Tempo de duração pra ser consumida (Após isso vai para DLQ)
                    .deadLetterExchange(exchangePropostaPendenteDLQ) //Direcionando para a DLQ
                    .build();
        }

        // - Queue proposta pendente notificação
        @Bean
        public Queue criarFilaPropostaPendenteMsNotificacao(){
            return QueueBuilder.durable(queuePropostaPendenteToNotificacao).build();
        }

        // - Queue proposta concluída
        @Bean
        public Queue criarFilaPropostaConcluidaMsProposta(){
            return QueueBuilder.durable(queuePropostaConcluida).build();
        }

        // - Queue proposta concluída notificação
        @Bean
        public Queue criarFilaPropostaConcluidaMsNotificacao(){
            return QueueBuilder.durable(queuePropostaConcluidaToNotificacao).build();
        }

        // - DLQ -> Dead Letter Queue (Fila de exceções)
        @Bean
        public Queue criarFilaPropostaPendenteDLQ(){
            return QueueBuilder.durable(queuePropostaPendenteDLQ).build();
        }


        //Defining Biding's (Redirecionamentos)

        // - Bind "PropostaPendenteMsAnaliseCredito"
        @Bean
        public Binding criarBindingPropostaPendenteMSAnaliseCredito(){
            return BindingBuilder.bind(criarFilaPropostaPendenteMsAnaliseCredito())
                    .to(criarFanoutExchangePropostaPendente());
        }

        // - Bind "PropostaPendenteMsNotificacao"
        @Bean
        public Binding criarBindingPropostaPendenteMSNotificacao(){
            return BindingBuilder.bind(criarFilaPropostaPendenteMsNotificacao())
                    .to(criarFanoutExchangePropostaPendente());
        }

        // - Bind "PropostaConcluidaMsPropostaMsProposta"
        @Bean
        public Binding criarBindingPropostaConcluidaMSPropostaApp(){
            return BindingBuilder.bind(criarFilaPropostaConcluidaMsProposta())
                    .to(criarFanoutExchangePropostaConcluida());
        }

        // - Bind "PropostaConcluidaMsNotificacao"
        @Bean
        public Binding criarBindingPropostaConcluidaMSNotificacao(){
            return BindingBuilder.bind(criarFilaPropostaConcluidaMsNotificacao())
                    .to(criarFanoutExchangePropostaConcluida());
        }

        // - Bind DLQ
        @Bean
        public Binding criarBindingPropostaPendenteDLQ(){
            return BindingBuilder.bind(criarFilaPropostaPendenteDLQ()).to(deadLetterExchange());
        }





        /*
        Criando configuration para connection factory

        private ConnectionFactory connectionFactory;

        public RabbitMQConfiguration(ConnectionFactory connectionFactory){
            this.connectionFactory = connectionFactory;
        }
        */


        // -------- Setando instâncias de configuração --------- //

        // Criando Bean Configuration do RabbitMQAdmin, para criação e gerência das filas por parte do Spring
        @Bean
        public RabbitAdmin criarRabbitAdmin(ConnectionFactory connectionFactory){
            return new RabbitAdmin(connectionFactory);
        }


        // Criando Bean para inicializar a criação de filas
        @Bean
        public ApplicationListener<ApplicationReadyEvent> inicializarAdmin(RabbitAdmin rabbitAdmin){
            return event->rabbitAdmin.initialize();
        }


        // -------- Defining Message Converter --------- //

        //Criado um Bean MessageConverter com o Jackson para converter Message do Rabbit em JSON e usar no @Bean de RabbitTemplate criado
        @Bean
        public MessageConverter jackson2JsonMessageConverter(){
            return new Jackson2JsonMessageConverter();
        }

        //Criando Bean de RabbitTemplate - Modelo de mensagem Rabbit
        // Definindo ConectionFactory
        // Definindo MessageConverter
        @Bean
        public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
            RabbitTemplate rabbitTemplate = new RabbitTemplate();
            rabbitTemplate.setConnectionFactory(connectionFactory);
            rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());

            return rabbitTemplate;
        }

}
