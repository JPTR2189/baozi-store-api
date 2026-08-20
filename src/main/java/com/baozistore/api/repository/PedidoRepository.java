package com.baozistore.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.baozistore.api.model.Pedido;

/**
 * Repository de Pedido.
 *
 * Alem do CRUD herdado de JpaRepository, declara dois metodos de consulta
 * derivados do nome ("query methods"): o Spring Data traduz o nome do metodo
 * em uma consulta, sem precisar de SQL escrito a mao. Sao usados para impedir
 * a exclusao de um cliente ou produto que ainda possua pedidos vinculados.
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    /** Existe algum pedido feito por este cliente? */
    boolean existsByClienteId(Long clienteId);

    /** Existe algum pedido com este produto? */
    boolean existsByProdutoId(Long produtoId);
}
