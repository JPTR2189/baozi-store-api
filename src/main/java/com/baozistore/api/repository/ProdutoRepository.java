package com.baozistore.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.baozistore.api.model.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
