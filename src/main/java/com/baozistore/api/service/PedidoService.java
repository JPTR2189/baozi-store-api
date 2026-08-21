package com.baozistore.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozistore.api.dto.PedidoRequest;
import com.baozistore.api.exception.RecursoNaoEncontradoException;
import com.baozistore.api.exception.RegraDeNegocioException;
import com.baozistore.api.model.Cliente;
import com.baozistore.api.model.Pedido;
import com.baozistore.api.model.Produto;
import com.baozistore.api.repository.ClienteRepository;
import com.baozistore.api.repository.PedidoRepository;
import com.baozistore.api.repository.ProdutoRepository;

@Service
@Transactional
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         ClienteRepository clienteRepository,
                         ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    public Pedido criar(PedidoRequest requisicao) {
        Pedido pedido = montarPedido(new Pedido(), requisicao);
        return pedidoRepository.save(pedido);
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido", id));
    }

    public Pedido atualizar(Long id, PedidoRequest requisicao) {
        Pedido pedido = buscarPorId(id);
        montarPedido(pedido, requisicao);
        return pedidoRepository.save(pedido);
    }

    // Atualiza apenas os campos informados (PATCH).
    public Pedido atualizarParcial(Long id, PedidoRequest requisicao) {
        Pedido pedido = buscarPorId(id);

        if (requisicao.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(requisicao.getClienteId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", requisicao.getClienteId()));
            pedido.setCliente(cliente);
        }

        if (requisicao.getProdutoId() != null) {
            Produto produto = produtoRepository.findById(requisicao.getProdutoId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", requisicao.getProdutoId()));
            if (Boolean.FALSE.equals(produto.getEstoque())) {
                throw new RegraDeNegocioException(
                        "O produto '" + produto.getNome() + "' esta indisponivel em estoque");
            }
            pedido.setProduto(produto);
        }

        if (requisicao.getQuantidade() != null) {
            pedido.setQuantidade(requisicao.getQuantidade());
        }

        return pedidoRepository.save(pedido);
    }

    public void apagar(Long id) {
        Pedido pedido = buscarPorId(id);
        pedidoRepository.delete(pedido);
    }

    // Resolve IDs do request para entidades e aplica regras de negocio.
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
