package com.baozistore.api.controller;

import java.net.URI;
import java.time.LocalDate;
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
import com.baozistore.api.model.Cliente;
import com.baozistore.api.repository.ClienteRepository;
import com.baozistore.api.repository.PedidoRepository;

import jakarta.validation.Valid;

/**
 * Endpoints REST de Cliente - a camada Controller do MVC do Spring.
 *
 * @RestController = @Controller + @ResponseBody: o retorno de cada metodo e
 * serializado automaticamente para JSON pelo Jackson.
 */
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;

    /** Injecao de dependencia por construtor (forma recomendada pelo Spring). */
    public ClienteController(ClienteRepository clienteRepository,
                             PedidoRepository pedidoRepository) {
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
    }

    /**
     * POST /api/clientes - cadastra um novo cliente.
     * Responde 201 Created com o cabecalho Location apontando para o recurso criado.
     */
    @PostMapping
    public ResponseEntity<Cliente> criar(@Valid @RequestBody Cliente cliente) {
        // O id e sempre gerado pelo banco: ignora qualquer id enviado no corpo.
        cliente.setId(null);

        // Regra RN6: se a data nao for informada, assume hoje.
        if (cliente.getClienteDesde() == null) {
            cliente.setClienteDesde(LocalDate.now());
        }

        Cliente salvo = clienteRepository.save(cliente);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();

        return ResponseEntity.created(location).body(salvo);
    }

    /** GET /api/clientes - lista todos os clientes cadastrados. */
    @GetMapping
    public ResponseEntity<List<Cliente>> listarTodos() {
        return ResponseEntity.ok(clienteRepository.findAll());
    }

    /** GET /api/clientes/{id} - consulta um cliente pelo seu ID. */
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", id));
        return ResponseEntity.ok(cliente);
    }

    /** PUT /api/clientes/{id} - atualiza os dados de um cliente (endpoint opcional). */
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizar(@PathVariable Long id,
                                             @Valid @RequestBody Cliente dados) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", id));

        cliente.setNome(dados.getNome());
        if (dados.getClienteDesde() != null) {
            cliente.setClienteDesde(dados.getClienteDesde());
        }

        return ResponseEntity.ok(clienteRepository.save(cliente));
    }

    /**
     * DELETE /api/clientes/{id} - apaga um cliente.
     * Regra RN5: um cliente com pedidos vinculados nao pode ser excluido.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", id));

        if (pedidoRepository.existsByClienteId(id)) {
            throw new RegraDeNegocioException(
                    "Nao e possivel excluir o cliente de id " + id + ": existem pedidos vinculados a ele");
        }

        clienteRepository.delete(cliente);
        return ResponseEntity.noContent().build();
    }
}
