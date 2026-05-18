package Establecimiento.Establecimiento.Service;

import Establecimiento.Establecimiento.Model.Establecimiento;
import Establecimiento.Establecimiento.Repository.EstablecimientoRepository;
import Establecimiento.Establecimiento.dto.EstablecimientoRequestDTO;
import Establecimiento.Establecimiento.dto.EstablecimientoResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class EstablecimientoService {

    @Autowired
    private EstablecimientoRepository establecimientoRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    private EstablecimientoResponseDTO mapToDTO(Establecimiento e) {
        return new EstablecimientoResponseDTO(
                e.getId(),
                e.getNombre(),
                e.getDireccion(),
                null,
                null
        );
    }
    private EstablecimientoResponseDTO mapToDTOCompleto(Establecimiento e) {
        EstablecimientoResponseDTO dto = mapToDTO(e);
        dto.setEntrenadores(obtenerEntrenadores(e.getId()));
        dto.setClientes(obtenerClientes(e.getId()));
        return dto;
    }
    public List<EstablecimientoResponseDTO> obtenerTodos() {
        return establecimientoRepository.findAll()
                .stream()
                .map(this::mapToDTOCompleto)
                .collect(Collectors.toList());
    }

    public Optional<EstablecimientoResponseDTO> obtenerPorId(Long id) {
        return establecimientoRepository.findById(id).map(this::mapToDTOCompleto);
    }


    public EstablecimientoResponseDTO guardar(EstablecimientoRequestDTO dto) {
        Establecimiento e = new Establecimiento();
        e.setNombre(dto.getNombre());
        e.setDireccion(dto.getDireccion());
        return mapToDTO(establecimientoRepository.save(e));
    }

    public void eliminarPorId(Long id) {
        establecimientoRepository.deleteById(id);
    }

    public List<Object> obtenerEntrenadores(Long establecimientoId) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8082/api/entrenadores/establecimiento/{id}", establecimientoId)
                    .retrieve()
                    .bodyToFlux(Object.class)
                    .collectList()
                    .block();
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<Object> obtenerClientes(Long establecimientoId) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8081/v1/clientes/establecimiento/{id}", establecimientoId)
                    .retrieve()
                    .bodyToFlux(Object.class)
                    .collectList()
                    .block();
        } catch (Exception e) {
            return List.of();
        }
    }
}