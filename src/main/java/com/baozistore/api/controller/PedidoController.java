package com.baozistore.api.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.baozistore.api.dto.PedidoRequest;
import com.baozistore.api.dto.PedidoResponse;
import com.baozistore.api.model.Pedido;
import com.baozistore.api.service.PedidoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> criar(@Valid @RequestBody PedidoRequest requisicao) {
        Pedido salvo = pedidoService.criar(requisicao);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();

        return ResponseEntity.created(location).body(PedidoResponse.de(salvo));
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponse>> listarTodos() {
        List<PedidoResponse> pedidos = pedidoService.listarTodos()
                .stream()
                .map(PedidoResponse::de)
                .toList();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(PedidoResponse.de(pedidoService.buscarPorId(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponse> atualizar(@PathVariable Long id,
                                                    @Valid @RequestBody PedidoRequest requisicao) {
        Pedido atualizado = pedidoService.atualizar(id, requisicao);
        return ResponseEntity.ok(PedidoResponse.de(atualizado));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PedidoResponse> atualizarParcial(@PathVariable Long id,
                                                           @RequestBody PedidoRequest requisicao) {
        Pedido atualizado = pedidoService.atualizarParcial(id, requisicao);
        return ResponseEntity.ok(PedidoResponse.de(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        pedidoService.apagar(id);
        return ResponseEntity.noContent().build();
    }
}
