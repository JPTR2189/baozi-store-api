package com.baozistore.api.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozistore.api.exception.RecursoNaoEncontradoException;
import com.baozistore.api.exception.RegraDeNegocioException;
import com.baozistore.api.model.Cliente;
import com.baozistore.api.repository.ClienteRepository;
import com.baozistore.api.repository.PedidoRepository;

@Service
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;

    public ClienteService(ClienteRepository clienteRepository,
                          PedidoRepository pedidoRepository) {
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public Cliente criar(Cliente cliente) {
        cliente.setId(null);
        if (cliente.getClienteDesde() == null) {
            cliente.setClienteDesde(LocalDate.now());
        }
        return clienteRepository.save(cliente);
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", id));
    }

    public Cliente atualizar(Long id, Cliente dados) {
        Cliente cliente = buscarPorId(id);
        cliente.setNome(dados.getNome());
        if (dados.getClienteDesde() != null) {
            cliente.setClienteDesde(dados.getClienteDesde());
        }
        return clienteRepository.save(cliente);
    }

    // Atualiza apenas os campos informados (PATCH).
    public Cliente atualizarParcial(Long id, Cliente dados) {
        Cliente cliente = buscarPorId(id);
        if (dados.getNome() != null) {
            cliente.setNome(dados.getNome());
        }
        if (dados.getClienteDesde() != null) {
            cliente.setClienteDesde(dados.getClienteDesde());
        }
        return clienteRepository.save(cliente);
    }

    public void apagar(Long id) {
        Cliente cliente = buscarPorId(id);
        if (pedidoRepository.existsByClienteId(id)) {
            throw new RegraDeNegocioException(
                    "Nao e possivel excluir o cliente de id " + id + ": existem pedidos vinculados a ele");
        }
        clienteRepository.delete(cliente);
    }
}
