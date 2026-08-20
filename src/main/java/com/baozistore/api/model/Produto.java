package com.baozistore.api.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Entidade Produto - o pao chines vendido pela Baozi Store.
 *
 * Mapeada para a tabela "produto" conforme o DER do enunciado:
 *   id (Long) | nome (String) | preco (BigDecimal) | estoque (Boolean)
 */
@Entity
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do produto e obrigatorio")
    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    /**
     * Preco unitario. Usa-se BigDecimal, e nao double, porque valores
     * monetarios exigem precisao decimal exata.
     */
    @NotNull(message = "O preco do produto e obrigatorio")
    @Positive(message = "O preco deve ser maior que zero")
    @Column(name = "preco", nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    /**
     * Indica se o produto esta disponivel para venda.
     * true = disponivel em estoque; false = indisponivel.
     */
    @Column(name = "estoque", nullable = false)
    private Boolean estoque;

    public Produto() {
    }

    public Produto(String nome, BigDecimal preco, Boolean estoque) {
        this.nome = nome;
        this.preco = preco;
        this.estoque = estoque;
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

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Boolean getEstoque() {
        return estoque;
    }

    public void setEstoque(Boolean estoque) {
        this.estoque = estoque;
    }

    @Override
    public String toString() {
        return "Produto{id=" + id + ", nome='" + nome + "', preco=" + preco + ", estoque=" + estoque + "}";
    }
}
