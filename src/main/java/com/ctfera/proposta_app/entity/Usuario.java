package com.ctfera.proposta_app.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tb_usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Geração de chaves primárias fica à cargo do SGBD
    private Long id;
    private String nome;
    private String sobrenome;
    private String cpf;
    private String telefone;
    private Double renda;

    @OneToOne(mappedBy = "usuario") //Relacionamento 1x1 com Proposta, mapeando usuario como nome do campo referente ao objeto Usuario em Proposta
    @JsonBackReference  //Para evitar loop infinito de referencia. Essa Annotation define o lado fraco do relacionamento, lado referenciado. (O usuário é referenciado na proposta, e não o contrário).
    private Proposta proposta; //Declaração de atributo como referência, o relacionamento é presente do lado forte, proposta.
}
