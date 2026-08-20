package com.baozistore.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.baozistore.api.model.Cliente;

/**
 * Repository de Cliente.
 *
 * Ao estender JpaRepository<Cliente, Long>, o Spring Data JPA gera em tempo de
 * execucao a implementacao com save, findById, findAll, deleteById, count etc.
 * Nao e necessario escrever nenhuma linha de SQL.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
