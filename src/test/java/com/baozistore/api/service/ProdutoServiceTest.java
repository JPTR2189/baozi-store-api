package com.baozistore.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.baozistore.api.exception.RecursoNaoEncontradoException;
import com.baozistore.api.exception.RegraDeNegocioException;
import com.baozistore.api.model.Produto;
import com.baozistore.api.repository.PedidoRepository;
import com.baozistore.api.repository.ProdutoRepository;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    @Test
    void criarDeveAssumirEstoqueDisponivelQuandoNulo() {
        Produto produto = new Produto("Baozi Carne", new BigDecimal("5.00"), null);
        when(produtoRepository.save(any(Produto.class))).thenAnswer(inv -> inv.getArgument(0));

        Produto salvo = produtoService.criar(produto);

        assertTrue(salvo.getEstoque());
    }

    @Test
    void buscarPorIdDeveLancarExcecaoQuandoNaoExistir() {
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> produtoService.buscarPorId(99L));
    }

    @Test
    void atualizarDeveModificarCampos() {
        Produto existente = new Produto("Baozi Original", new BigDecimal("5.00"), true);
        existente.setId(1L);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(produtoRepository.save(any(Produto.class))).thenAnswer(inv -> inv.getArgument(0));

        Produto dados = new Produto("Baozi Premium", new BigDecimal("8.00"), true);
        Produto atualizado = produtoService.atualizar(1L, dados);

        assertEquals("Baozi Premium", atualizado.getNome());
        assertEquals(new BigDecimal("8.00"), atualizado.getPreco());
    }

    @Test
    void apagarDeveLancarExcecaoQuandoExistiremPedidos() {
        Produto produto = new Produto("Baozi Carne", new BigDecimal("5.00"), true);
        produto.setId(1L);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(pedidoRepository.existsByProdutoId(1L)).thenReturn(true);

        assertThrows(RegraDeNegocioException.class,
                () -> produtoService.apagar(1L));
        verify(produtoRepository, never()).delete(any());
    }
}
