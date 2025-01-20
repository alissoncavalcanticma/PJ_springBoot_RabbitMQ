package com.ctfera.proposta_app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//Para ativar o uso do Cors deve implementar a interface WebMvcConfigurer
@Configuration  // Definindo annotation de configuração
@EnableWebMvc
public class CorsConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**") // Informando que tudo após a raiz deve ser permitido
                .allowedOrigins("*") //Informando o host/origem
                //.allowedOrigins("http://localhost:8080/")
                //.allowedMethods("*");
               .allowedMethods("GET", "POST", "PUT", "DELETE");

    }

}
