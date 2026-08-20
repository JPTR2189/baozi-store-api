package com.baozistore.api.dto;

import java.math.BigDecimal;

import com.baozistore.api.model.Pedido;

/**
 * Corpo JSON devolvido pelos endpoints de /api/pedidos.
 *
 * Achata a associacao @ManyToOne para que o print do Postman mostre de forma
 * legivel, em um unico bloco, exatamente o que o enunciado pede que apareca:
 * o cliente que comprou, o produto comprado e a quantidade.
 *
 * O campo valorTotal e DERIVADO (preco unitario x quantidade). Ele e calculado
 * aqui, na saida, e NAO e uma coluna da tabela pedido - o modelo continua fiel
 * ao DER do enunciado.
 */
public class PedidoResponse {

    private Long id;
    private Long clienteId;
    private String clienteNome;
    private Long produtoId;
    private String produtoNome;
    private BigDecimal precoUnitario;
    private Integer quantidade;
    private BigDecimal valorTotal;

    public PedidoResponse() {
    }

    /** Converte a entidade JPA na representacao JSON de saida. */
    public static PedidoResponse de(Pedido pedido) {
        PedidoResponse dto = new PedidoResponse();
        dto.id = pedido.getId();
        dto.quantidade = pedido.getQuantidade();

        if (pedido.getCliente() != null) {
            dto.clienteId = pedido.getCliente().getId();
            dto.clienteNome = pedido.getCliente().getNome();
        }
        if (pedido.getProduto() != null) {
            dto.produtoId = pedido.getProduto().getId();
            dto.produtoNome = pedido.getProduto().getNome();
            dto.precoUnitario = pedido.getProduto().getPreco();

            if (dto.precoUnitario != null && dto.quantidade != null) {
                dto.valorTotal = dto.precoUnitario.multiply(BigDecimal.valueOf(dto.quantidade));
            }
        }
        return dto;
    }

    public Long getId() {
        return id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public String getProdutoNome() {
        return produtoNome;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }
}
