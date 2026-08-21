package com.baozistore.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozistore.api.exception.RecursoNaoEncontradoException;
import com.baozistore.api.exception.RegraDeNegocioException;
import com.baozistore.api.model.Produto;
import com.baozistore.api.repository.PedidoRepository;
import com.baozistore.api.repository.ProdutoRepository;

@Service
@Transactional
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final PedidoRepository pedidoRepository;

    public ProdutoService(ProdutoRepository produtoRepository,
                          PedidoRepository pedidoRepository) {
        this.produtoRepository = produtoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public Produto criar(Produto produto) {
        produto.setId(null);
        if (produto.getEstoque() == null) {
            produto.setEstoque(Boolean.TRUE);
        }
        return produtoRepository.save(produto);
    }

    @Transactional(readOnly = true)
    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", id));
    }

    public Produto atualizar(Long id, Produto dados) {
        Produto produto = buscarPorId(id);
        produto.setNome(dados.getNome());
        produto.setPreco(dados.getPreco());
        if (dados.getEstoque() != null) {
            produto.setEstoque(dados.getEstoque());
        }
        return produtoRepository.save(produto);
    }

    // Atualiza apenas os campos informados (PATCH).
    public Produto atualizarParcial(Long id, Produto dados) {
        Produto produto = buscarPorId(id);
        if (dados.getNome() != null) {
            produto.setNome(dados.getNome());
        }
        if (dados.getPreco() != null) {
            produto.setPreco(dados.getPreco());
        }
        if (dados.getEstoque() != null) {
            produto.setEstoque(dados.getEstoque());
        }
        return produtoRepository.save(produto);
    }

    public void apagar(Long id) {
        Produto produto = buscarPorId(id);
        if (pedidoRepository.existsByProdutoId(id)) {
            throw new RegraDeNegocioException(
                    "Nao e possivel excluir o produto de id " + id + ": existem pedidos vinculados a ele");
        }
        produtoRepository.delete(produto);
    }
}
