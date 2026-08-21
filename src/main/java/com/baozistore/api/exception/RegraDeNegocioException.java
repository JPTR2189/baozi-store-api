package com.baozistore.api.exception;

// Lancada quando a operacao viola uma regra de negocio (HTTP 409).
public class RegraDeNegocioException extends RuntimeException {

    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
