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

import com.baozistore.api.exception.RecursoNaoEncontradoException;
import com.baozistore.api.exception.RegraDeNegocioException;
import com.baozistore.api.model.Produto;
import com.baozistore.api.repository.PedidoRepository;
import com.baozistore.api.repository.ProdutoRepository;

import jakarta.validation.Valid;

/**
 * Endpoints REST de Produto - camada Controller do MVC do Spring.
 */
@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoRepository produtoRepository;
    private final PedidoRepository pedidoRepository;

    public ProdutoController(ProdutoRepository produtoRepository,
                             PedidoRepository pedidoRepository) {
        this.produtoRepository = produtoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    /** POST /api/produtos - cadastra um novo produto. Responde 201 Created. */
    @PostMapping
    public ResponseEntity<Produto> criar(@Valid @RequestBody Produto produto) {
        produto.setId(null);

        // Se o campo estoque nao for informado, o produto entra como disponivel.
        if (produto.getEstoque() == null) {
            produto.setEstoque(Boolean.TRUE);
        }

        Produto salvo = produtoRepository.save(produto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();

        return ResponseEntity.created(location).body(salvo);
    }

    /** GET /api/produtos - lista todos os produtos. */
    @GetMapping
    public ResponseEntity<List<Produto>> listarTodos() {
        return ResponseEntity.ok(produtoRepository.findAll());
    }

    /** GET /api/produtos/{id} - consulta um produto pelo seu ID. */
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", id));
        return ResponseEntity.ok(produto);
    }

    /** PUT /api/produtos/{id} - atualiza um produto (endpoint opcional). */
    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(@PathVariable Long id,
                                             @Valid @RequestBody Produto dados) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", id));

        produto.setNome(dados.getNome());
        produto.setPreco(dados.getPreco());
        if (dados.getEstoque() != null) {
            produto.setEstoque(dados.getEstoque());
        }

        return ResponseEntity.ok(produtoRepository.save(produto));
    }

    /**
     * DELETE /api/produtos/{id} - apaga um produto.
     * Regra RN5: um produto com pedidos vinculados nao pode ser excluido.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", id));

        if (pedidoRepository.existsByProdutoId(id)) {
            throw new RegraDeNegocioException(
                    "Nao e possivel excluir o produto de id " + id + ": existem pedidos vinculados a ele");
        }

        produtoRepository.delete(produto);
        return ResponseEntity.noContent().build();
    }
}
