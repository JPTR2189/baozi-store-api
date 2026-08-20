package com.baozistore.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Entidade Pedido - a compra de UM produto por UM cliente, em determinada quantidade.
 *
 * Mapeada para a tabela "pedido" conforme o DER do enunciado:
 *   id (Long) | clienteId (Long) | produtoId (Long) | quantidade (Integer)
 *
 * As colunas clienteId e produtoId do DER sao implementadas como associacoes
 * @ManyToOne com @JoinColumn, o que gera no banco exatamente as colunas
 * cliente_id e produto_id, agora como CHAVES ESTRANGEIRAS de verdade
 * (com integridade referencial garantida pelo banco).
 *
 * Cardinalidade do DER:
 *   - um Cliente FAZ varios Pedidos      -> muitos Pedidos para um Cliente;
 *   - um Produto e VENDIDO EM varios Pedidos -> muitos Pedidos para um Produto.
 */
@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Cliente que realizou a compra. Coluna cliente_id no banco.
     *
     * fetch = EAGER (o padrao do @ManyToOne): o cliente e carregado junto com o
     * pedido. Isso e proposital aqui - a resposta JSON de todo pedido sempre
     * exibe o nome do cliente, e com open-in-view desligado um carregamento
     * preguicoso (LAZY) lancaria LazyInitializationException ao montar o DTO
     * fora da transacao. O volume de dados desta loja e pequeno, entao o custo
     * do EAGER e irrelevante.
     */
    @NotNull(message = "O cliente do pedido e obrigatorio")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    /** Produto comprado. Coluna produto_id no banco. Mesmo raciocinio do campo cliente. */
    @NotNull(message = "O produto do pedido e obrigatorio")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    /** Quantidade de unidades compradas. Deve ser pelo menos 1. */
    @NotNull(message = "A quantidade e obrigatoria")
    @Positive(message = "A quantidade deve ser maior que zero")
    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    public Pedido() {
    }

    public Pedido(Cliente cliente, Produto produto, Integer quantidade) {
        this.cliente = cliente;
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    @Override
    public String toString() {
        return "Pedido{id=" + id + ", quantidade=" + quantidade + "}";
    }
}
