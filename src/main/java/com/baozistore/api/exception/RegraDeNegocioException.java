package com.baozistore.api.exception;

/**
 * Lancada quando a operacao e sintaticamente valida mas viola uma regra de
 * negocio da loja (por exemplo: pedir um produto fora de estoque, ou excluir
 * um cliente que ainda possui pedidos).
 * O GlobalExceptionHandler a converte em uma resposta HTTP 409 Conflict.
 */
public class RegraDeNegocioException extends RuntimeException {

    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
