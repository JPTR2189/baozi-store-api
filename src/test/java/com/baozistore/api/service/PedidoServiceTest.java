package com.baozistore.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.baozistore.api.dto.PedidoRequest;
import com.baozistore.api.exception.RecursoNaoEncontradoException;
import com.baozistore.api.exception.RegraDeNegocioException;
import com.baozistore.api.model.Cliente;
import com.baozistore.api.model.Pedido;
import com.baozistore.api.model.Produto;
import com.baozistore.api.repository.ClienteRepository;
import com.baozistore.api.repository.PedidoRepository;
import com.baozistore.api.repository.ProdutoRepository;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private Cliente clienteValido() {
        Cliente c = new Cliente("Ana", LocalDate.now());
        c.setId(1L);
        return c;
    }

    private Produto produtoDisponivel() {
        Produto p = new Produto("Baozi Carne", new BigDecimal("5.00"), true);
        p.setId(1L);
        return p;
    }

    private PedidoRequest requisicaoValida() {
        PedidoRequest req = new PedidoRequest();
        req.setClienteId(1L);
        req.setProdutoId(1L);
        req.setQuantidade(10);
        return req;
    }

    @Test
    void criarDeveSalvarPedidoComDadosCorretos() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteValido()));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoDisponivel()));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> {
            Pedido p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        Pedido salvo = pedidoService.criar(requisicaoValida());

        assertNotNull(salvo.getId());
        assertEquals("Ana", salvo.getCliente().getNome());
        assertEquals(10, salvo.getQuantidade());
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void criarDeveLancarExcecaoQuandoClienteNaoExistir() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> pedidoService.criar(requisicaoValida()));
    }

    @Test
    void criarDeveLancarExcecaoQuandoProdutoForaDeEstoque() {
        Produto indisponivel = new Produto("Baozi Vegano", new BigDecimal("6.00"), false);
        indisponivel.setId(1L);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteValido()));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(indisponivel));

        assertThrows(RegraDeNegocioException.class,
                () -> pedidoService.criar(requisicaoValida()));
    }

    @Test
    void buscarPorIdDeveLancarExcecaoQuandoNaoExistir() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> pedidoService.buscarPorId(99L));
    }

    @Test
    void apagarDeveRemoverPedido() {
        Pedido pedido = new Pedido(clienteValido(), produtoDisponivel(), 5);
        pedido.setId(1L);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        pedidoService.apagar(1L);

        verify(pedidoRepository).delete(pedido);
    }
}
