package com.baozistore.api.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.baozistore.api.dto.PedidoRequest;
import com.baozistore.api.dto.PedidoResponse;
import com.baozistore.api.exception.RecursoNaoEncontradoException;
import com.baozistore.api.exception.RegraDeNegocioException;
import com.baozistore.api.model.Cliente;
import com.baozistore.api.model.Pedido;
import com.baozistore.api.model.Produto;
import com.baozistore.api.repository.ClienteRepository;
import com.baozistore.api.repository.PedidoRepository;
import com.baozistore.api.repository.ProdutoRepository;

import jakarta.validation.Valid;

/**
 * Endpoints REST de Pedido - camada Controller do MVC do Spring.
 *
 * Um pedido registra: o cliente que comprou, o produto comprado e a quantidade.
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoController(PedidoRepository pedidoRepository,
                            ClienteRepository clienteRepository,
                            ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    /**
     * POST /api/pedidos - registra um novo pedido.
     * Corpo: { "clienteId": 1, "produtoId": 1, "quantidade": 10 }
     */
    @PostMapping
    public ResponseEntity<PedidoResponse> criar(@Valid @RequestBody PedidoRequest requisicao) {
        Pedido pedido = montarPedido(new Pedido(), requisicao);
        Pedido salvo = pedidoRepository.save(pedido);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();

        return ResponseEntity.created(location).body(PedidoResponse.de(salvo));
    }

    /** GET /api/pedidos - lista todos os pedidos registrados. */
    @GetMapping
    public ResponseEntity<List<PedidoResponse>> listarTodos() {
        List<PedidoResponse> pedidos = pedidoRepository.findAll()
                .stream()
                .map(PedidoResponse::de)
                .toList();
        return ResponseEntity.ok(pedidos);
    }

    /** GET /api/pedidos/{id} - consulta um pedido pelo seu ID. */
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> buscarPorId(@PathVariable Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido", id));
        return ResponseEntity.ok(PedidoResponse.de(pedido));
    }

    /** PUT /api/pedidos/{id} - atualiza um pedido (endpoint opcional). */
    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponse> atualizar(@PathVariable Long id,
                                                    @Valid @RequestBody PedidoRequest requisicao) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido", id));

        montarPedido(pedido, requisicao);
        return ResponseEntity.ok(PedidoResponse.de(pedidoRepository.save(pedido)));
    }

    /** DELETE /api/pedidos/{id} - apaga um pedido. Responde 204 No Content. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido", id));

        pedidoRepository.delete(pedido);
        return ResponseEntity.noContent().build();
    }

    // ------------------------------------------------------------------
    //  Metodo auxiliar compartilhado pelo POST e pelo PUT
    // ------------------------------------------------------------------

    /**
     * Resolve os IDs recebidos no JSON para as entidades reais e aplica as
     * regras de negocio do pedido.
     *
     * RN3: cliente e produto precisam existir  -> 404 Not Found.
     * RN4: o produto precisa estar em estoque  -> 409 Conflict.
     */
    private Pedido montarPedido(Pedido pedido, PedidoRequest requisicao) {
        Cliente cliente = clienteRepository.findById(requisicao.getClienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", requisicao.getClienteId()));

        Produto produto = produtoRepository.findById(requisicao.getProdutoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", requisicao.getProdutoId()));

        if (Boolean.FALSE.equals(produto.getEstoque())) {
            throw new RegraDeNegocioException(
                    "O produto '" + produto.getNome() + "' esta indisponivel em estoque");
        }

        pedido.setCliente(cliente);
        pedido.setProduto(produto);
        pedido.setQuantidade(requisicao.getQuantidade());
        return pedido;
    }
}
