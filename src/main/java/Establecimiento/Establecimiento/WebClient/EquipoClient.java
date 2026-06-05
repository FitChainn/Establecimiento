package Establecimiento.Establecimiento.WebClient;

import Establecimiento.Establecimiento.dto.EquipoRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@Component
@RequiredArgsConstructor
public class EquipoClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${equipo.service.url}")
    private String equipoServiceUrl;

    // Obtiene resumen de equipos por establecimiento
    public Map<String, Object> obtenerResumenEquipos(Long establecimientoId) {
        log.info("Obteniendo resumen equipos del establecimiento {}", establecimientoId);
        try {
            Map result = webClientBuilder.build()
                    .get()
                    .uri(equipoServiceUrl + "/v1/equipos/establecimiento/" + establecimientoId + "/resumen")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            return result != null ? result : Map.of();
        } catch (WebClientResponseException.NotFound e) {
            log.error("No se encontraron equipos para establecimiento {}", establecimientoId);
            throw new NoSuchElementException("No se encontraron equipos para el establecimiento " + establecimientoId);
        } catch (Exception e) {
            log.error("Error al conectar con microservicio Equipo: {}", e.getMessage());
            throw new RuntimeException("Error al conectar con el microservicio de equipos");
        }
    }

    // Crea un equipo en un establecimiento
    public void crearEquipo(Long establecimientoId, EquipoRequestDTO dto) {
        log.info("Creando equipo en establecimiento {}", establecimientoId);
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("tipoMaquina", dto.getTipoMaquina());
            body.put("marca", dto.getMarca());
            body.put("fechaCompra", dto.getFechaCompra().toString());
            body.put("estado", dto.getEstado());
            body.put("establecimientoId", establecimientoId);

            webClientBuilder.build()
                    .post()
                    .uri(equipoServiceUrl + "/v1/equipos")
                    .bodyValue(body)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            log.info("Equipo creado correctamente en establecimiento {}", establecimientoId);
        } catch (WebClientResponseException.BadRequest e) {
            log.error("Datos inválidos al crear equipo: {}", e.getMessage());
            throw new RuntimeException("Datos inválidos para crear el equipo");
        } catch (Exception e) {
            log.error("Error al crear equipo: {}", e.getMessage());
            throw new RuntimeException("Error al conectar con el microservicio de equipos");
        }
    }
}