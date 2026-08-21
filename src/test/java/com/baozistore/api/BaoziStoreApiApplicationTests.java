package com.baozistore.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

// Teste de integracao do fluxo completo da API contra H2 em memoria.
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BaoziStoreApiApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private long idDaResposta(MvcResult resultado) throws Exception {
        JsonNode corpo = objectMapper.readTree(resultado.getResponse().getContentAsString());
        return corpo.get("id").asLong();
    }

    @Test
    void contextoDaAplicacaoCarrega() {
    }

    @Test
    void fluxoCompletoDeCadastroEPedido() throws Exception {
        MvcResult respostaCliente = mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Cliente de Teste\",\"clienteDesde\":\"2026-01-15\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Cliente de Teste"))
                .andReturn();
        long clienteId = idDaResposta(respostaCliente);

        MvcResult respostaProduto = mockMvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Baozi de Teste\",\"preco\":3.50,\"estoque\":true}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Baozi de Teste"))
                .andReturn();
        long produtoId = idDaResposta(respostaProduto);

        MvcResult respostaPedido = mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clienteId\":" + clienteId
                                + ",\"produtoId\":" + produtoId
                                + ",\"quantidade\":10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteNome").value("Cliente de Teste"))
                .andExpect(jsonPath("$.produtoNome").value("Baozi de Teste"))
                .andExpect(jsonPath("$.quantidade").value(10))
                .andReturn();
        long pedidoId = idDaResposta(respostaPedido);

        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());

        mockMvc.perform(get("/api/pedidos/" + pedidoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pedidoId));

        mockMvc.perform(delete("/api/pedidos/" + pedidoId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/pedidos/" + pedidoId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void pedidoComClienteInexistenteRetorna404() throws Exception {
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clienteId\":9999,\"produtoId\":9999,\"quantidade\":1}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void clienteSemNomeRetorna400() throws Exception {
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.nome").exists());
    }
}
