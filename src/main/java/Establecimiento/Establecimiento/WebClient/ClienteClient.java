package Establecimiento.Establecimiento.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClienteClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${cliente.service.url}")
    private String clienteServiceUrl;

    public List<Object> obtenerClientesPorEstablecimiento(Long establecimientoId) {
        log.info("Obteniendo clientes del establecimiento {}", establecimientoId);
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(clienteServiceUrl + "/v1/clientes/establecimiento/" + establecimientoId)
                    .retrieve()
                    .bodyToFlux(Object.class)
                    .collectList()
                    .block();
        } catch (Exception e) {
            log.warn("No se pudieron obtener clientes del establecimiento {}", establecimientoId);
            return List.of();
        }
    }
}