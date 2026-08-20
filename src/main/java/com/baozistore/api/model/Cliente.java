package com.baozistore.api.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * Entidade Cliente - representa quem compra na Baozi Store.
 *
 * Mapeada para a tabela "cliente" conforme o DER do enunciado:
 *   id (Long) | nome (String) | clienteDesde (LocalDate)
 */
@Entity
@Table(name = "cliente")
public class Cliente {

    /** Chave primaria gerada pelo proprio banco (auto-incremento). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nome do cliente. Obrigatorio. */
    @NotBlank(message = "O nome do cliente e obrigatorio")
    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    /**
     * Data em que a pessoa passou a ser cliente da loja.
     * Se nao for informada no POST, o controller assume a data de hoje.
     */
    @Column(name = "cliente_desde", nullable = false)
    private LocalDate clienteDesde;

    /** Construtor vazio exigido pela especificacao JPA. */
    public Cliente() {
    }

    public Cliente(String nome, LocalDate clienteDesde) {
        this.nome = nome;
        this.clienteDesde = clienteDesde;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getClienteDesde() {
        return clienteDesde;
    }

    public void setClienteDesde(LocalDate clienteDesde) {
        this.clienteDesde = clienteDesde;
    }

    @Override
    public String toString() {
        return "Cliente{id=" + id + ", nome='" + nome + "', clienteDesde=" + clienteDesde + "}";
    }
}
