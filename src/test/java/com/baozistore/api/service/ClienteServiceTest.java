package com.baozistore.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.baozistore.api.exception.RecursoNaoEncontradoException;
import com.baozistore.api.exception.RegraDeNegocioException;
import com.baozistore.api.model.Cliente;
import com.baozistore.api.repository.ClienteRepository;
import com.baozistore.api.repository.PedidoRepository;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void criarDeveAtribuirDataDeHojeQuandoClienteDesdeForNulo() {
        Cliente cliente = new Cliente("Ana", null);
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        Cliente salvo = clienteService.criar(cliente);

        assertNotNull(salvo.getClienteDesde());
        assertEquals(LocalDate.now(), salvo.getClienteDesde());
    }

    @Test
    void listarTodosDeveRetornarListaDoRepositorio() {
        List<Cliente> lista = List.of(new Cliente("Ana", LocalDate.now()));
        when(clienteRepository.findAll()).thenReturn(lista);

        assertEquals(1, clienteService.listarTodos().size());
    }

    @Test
    void buscarPorIdDeveLancarExcecaoQuandoNaoExistir() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> clienteService.buscarPorId(99L));
    }

    @Test
    void apagarDeveLancarExcecaoQuandoExistiremPedidos() {
        Cliente cliente = new Cliente("Ana", LocalDate.now());
        cliente.setId(1L);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(pedidoRepository.existsByClienteId(1L)).thenReturn(true);

        assertThrows(RegraDeNegocioException.class,
                () -> clienteService.apagar(1L));
        verify(clienteRepository, never()).delete(any());
    }

    @Test
    void apagarDeveRemoverQuandoNaoExistiremPedidos() {
        Cliente cliente = new Cliente("Ana", LocalDate.now());
        cliente.setId(1L);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(pedidoRepository.existsByClienteId(1L)).thenReturn(false);

        clienteService.apagar(1L);

        verify(clienteRepository).delete(cliente);
    }
}
