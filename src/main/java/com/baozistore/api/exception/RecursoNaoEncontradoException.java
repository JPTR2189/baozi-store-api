package com.baozistore.api.exception;

/**
 * Lancada quando um recurso solicitado por ID nao existe no banco.
 * O GlobalExceptionHandler a converte em uma resposta HTTP 404 Not Found.
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public RecursoNaoEncontradoException(String recurso, Long id) {
        super(recurso + " de id " + id + " nao encontrado(a)");
    }
}
