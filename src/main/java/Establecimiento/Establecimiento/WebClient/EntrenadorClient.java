package Establecimiento.Establecimiento.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Component
@RequiredArgsConstructor
public class EntrenadorClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${entrenador.service.url}")
    private String entrenadorServiceUrl;

    // Verifica que el entrenador existe
    public void verificarEntrenadorExiste(Long entrenadorId) {
        log.info("Verificando entrenador con id {}", entrenadorId);
        try {
            webClientBuilder.build()
                    .get()
                    .uri(entrenadorServiceUrl + "/v1/entrenadores/" + entrenadorId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new NoSuchElementException("El entrenador con ID " + entrenadorId + " no existe");
        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con el microservicio de entrenadores");
        }
    }

    // Obtiene entrenadores de un establecimiento
    public List<Object> obtenerEntrenadeoresPorEstablecimiento(Long establecimientoId) {
        log.info("Obteniendo entrenadores del establecimiento {}", establecimientoId);
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(entrenadorServiceUrl + "/v1/entrenadores/establecimiento/" + establecimientoId)
                    .retrieve()
                    .bodyToFlux(Object.class)
                    .collectList()
                    .block();
        } catch (Exception e) {
            log.warn("No se pudieron obtener entrenadores del establecimiento {}", establecimientoId);
            return List.of();
        }
    }

    // Asigna establecimiento a entrenador via endpoint interno de Entrenador
    public void asignarEstablecimientoAEntrenador(Long entrenadorId, Long establecimientoId) {
        log.info("Asignando establecimiento {} al entrenador {}", establecimientoId, entrenadorId);
        try {
            webClientBuilder.build()
                    .put()
                    .uri(entrenadorServiceUrl + "/v1/entrenadores/" + entrenadorId + "/establecimiento/" + establecimientoId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new NoSuchElementException("El entrenador con ID " + entrenadorId + " no existe");
        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con el microservicio de entrenadores");
        }
    }
}