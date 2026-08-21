package com.baozistore.api.exception;

// Lancada quando um recurso solicitado por ID nao existe no banco (HTTP 404).
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public RecursoNaoEncontradoException(String recurso, Long id) {
        super(recurso + " de id " + id + " nao encontrado(a)");
    }
}
