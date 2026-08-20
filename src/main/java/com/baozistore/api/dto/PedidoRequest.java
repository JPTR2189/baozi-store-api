package com.baozistore.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Corpo JSON esperado no POST e no PUT de /api/pedidos.
 *
 * Mantem o contrato de entrada simples e igual ao DER
 * (clienteId, produtoId, quantidade), sem exigir objetos aninhados:
 *
 *   { "clienteId": 1, "produtoId": 1, "quantidade": 10 }
 */
public class PedidoRequest {

    @NotNull(message = "O clienteId e obrigatorio")
    private Long clienteId;

    @NotNull(message = "O produtoId e obrigatorio")
    private Long produtoId;

    @NotNull(message = "A quantidade e obrigatoria")
    @Positive(message = "A quantidade deve ser maior que zero")
    private Integer quantidade;

    public PedidoRequest() {
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}
