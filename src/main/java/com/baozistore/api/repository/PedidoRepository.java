package com.baozistore.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.baozistore.api.model.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    boolean existsByClienteId(Long clienteId);

    boolean existsByProdutoId(Long produtoId);
}
