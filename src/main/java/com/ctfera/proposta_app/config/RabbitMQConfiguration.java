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


        //Instanciando Exchange e passado valor de property
        @Value("${spring.rabbitmq.propostapendente.exchange}")
        private String exchangePropostaPendente;

        @Value("${spring.rabbitmq.propostaconcluida.exchange}")
        private String exchangePropostaConcluida;

        //Criação das filas
        //Filas foram criadas nesse microserviço, mas deveriam ser criadas de acordo com a responsabilidade de cada microserviço.
        //@Bean é utilizado quando se quer passar a instância de response do método para a gerência do Spring
        //Obs: @Bean não passa o método, e sim o response dele para o Spring

        @Bean
        public Queue criarFilaPropostaPendenteMsAnaliseCredito(){
            return QueueBuilder.durable("proposta-pendente.ms-analise-credito")
                    .deadLetterExchange("proposta-pendente.dlq.ex") //configuração de DLQ
                    .build();
        }

        @Bean
        public Queue criarFilaPropostaPendenteMsNotificacao(){
            return QueueBuilder.durable("proposta-pendente.ms-notificacao").build();
        }

        @Bean
        public Queue criarFilaPropostaConcluidaMsProposta(){
            return QueueBuilder.durable("proposta-concluida.ms-proposta").build();
        }

        @Bean
        public Queue criarFilaPropostaConcluidaMsNotificacao(){
            return QueueBuilder.durable("proposta-concluida.ms-notificacao").build();
        }

        //DLQ Configurations
        // - Criando DLQ -> Dead Letter Queue (Fila de exceções)
        @Bean
        public Queue criarFilaPropostaPendenteDLQ(){
            return QueueBuilder.durable("proposta-pendente.dlq").build();
        }

        // - Criando Exchange DLQ
        @Bean
        public FanoutExchange deadLetterExchange(){
            return ExchangeBuilder.fanoutExchange("proposta-pendente.dlq.ex").build();
        }

        // - Criando Bind DLQ
        @Bean
        public Binding criarBinding(){
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


        // -------- Setando Exchanges e Bindings --------- //


        //Criando a Exchange "proposta-pendente"
        //Setando @Bean para entrega do response do método à gerência do Spring.

        @Bean
        public FanoutExchange criarFanoutExchangePropostaPendente(){
            return ExchangeBuilder.fanoutExchange(exchangePropostaPendente).build();
        }

        //Criando a Exchange "proposta-concluida"
        //Setando @Bean para entrega do response do método à gerência do Spring.

        @Bean
        public FanoutExchange criarFanoutExchangePropostaConcluida(){
            return ExchangeBuilder.fanoutExchange(exchangePropostaConcluida).build();
        }


        //Setando o binds da Exchange "proposta-pendente"
        //Método seta Queues da Exchange específica
        //"PropostaPendenteMsAnaliseCredito"
        @Bean
        public Binding criarBindingPropostaPendenteMSAnaliseCredito(){
            return BindingBuilder.bind(criarFilaPropostaPendenteMsAnaliseCredito())
                                        .to(criarFanoutExchangePropostaPendente());
        }

        //"PropostaPendenteMsNotificacao"
        @Bean
        public Binding criarBindingPropostaPendenteMSNotificacao(){
            return BindingBuilder.bind(criarFilaPropostaPendenteMsNotificacao())
                    .to(criarFanoutExchangePropostaPendente());
        }


        //Setando o binds da Exchange "proposta-concluida"
        //Método seta Queues da Exchange específica
        //"PropostaConcluidaMsPropostaMsProposta"
        @Bean
        public Binding criarBindingPropostaConcluidaMSPropostaApp(){
            return BindingBuilder.bind(criarFilaPropostaConcluidaMsProposta())
                    .to(criarFanoutExchangePropostaConcluida());
        }

        //"PropostaConcluidaMsNotificacao"
        @Bean
        public Binding criarBindingPropostaConcluidaMSNotificacao(){
            return BindingBuilder.bind(criarFilaPropostaConcluidaMsNotificacao())
                    .to(criarFanoutExchangePropostaConcluida());
        }




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
