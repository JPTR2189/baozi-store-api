package com.baozistore.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da API REST da Baozi Store.
 *
 * A anotacao @SpringBootApplication reune tres anotacoes:
 *  - @Configuration        : a classe pode declarar beans;
 *  - @EnableAutoConfiguration : o Spring Boot configura sozinho o Tomcat embarcado,
 *                            o DataSource do H2 e o Hibernate a partir do classpath;
 *  - @ComponentScan        : varre o pacote com.baozistore.api e seus subpacotes,
 *                            registrando controllers e repositories automaticamente.
 */
@SpringBootApplication
public class BaoziStoreApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaoziStoreApiApplication.class, args);
    }
}
