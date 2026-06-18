package Establecimiento.Establecimiento.Service;

import Establecimiento.Establecimiento.Model.Establecimiento;
import Establecimiento.Establecimiento.Repository.EstablecimientoRepository;
import Establecimiento.Establecimiento.WebClient.ClienteClient;
import Establecimiento.Establecimiento.WebClient.EntrenadorClient;
import Establecimiento.Establecimiento.WebClient.EquipoClient;
import Establecimiento.Establecimiento.dto.EquipoRequestDTO;
import Establecimiento.Establecimiento.dto.EstablecimientoRequestDTO;
import Establecimiento.Establecimiento.dto.EstablecimientoResponseDTO;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class EstablecimientoService {

    @Autowired
    private EstablecimientoRepository establecimientoRepository;
    @Autowired
    private EntrenadorClient entrenadorClient;
    @Autowired
    private ClienteClient clienteClient;
    @Autowired
    private EquipoClient equipoClient;

    private EstablecimientoResponseDTO mapToDTO(Establecimiento e) {
        return new EstablecimientoResponseDTO(
                e.getId(),
                e.getNombre(),
                e.getDireccion(),
                null,
                null,
                null
        );
    }

    private EstablecimientoResponseDTO mapToDTOCompleto(Establecimiento e) {
        EstablecimientoResponseDTO dto = mapToDTO(e);
        dto.setEntrenadores(entrenadorClient.obtenerEntrenadeoresPorEstablecimiento(e.getId()));
        dto.setClientes(clienteClient.obtenerClientesPorEstablecimiento(e.getId()));
        // Si no hay equipos, devuelve Map vacío en vez de romper
        try {
            dto.setResumenEquipos(equipoClient.obtenerResumenEquipos(e.getId()));
        } catch (NoSuchElementException ex) {
            log.warn("Sin equipos para establecimiento {}", e.getId());
            dto.setResumenEquipos(Map.of());
        }
        return dto;
    }

    public List<EstablecimientoResponseDTO> obtenerTodos() {
        log.info("OBTENIENDO TODOS LOS ESTABLECIMIENTOS");
        return establecimientoRepository.findAll()
                .stream()
                .map(this::mapToDTOCompleto)
                .collect(Collectors.toList());
    }

    public Optional<EstablecimientoResponseDTO> obtenerPorId(Long id) {
        log.info("BUSCANDO ESTABLECIMIENTO CON ID: {}", id);
        return establecimientoRepository.findById(id).map(this::mapToDTOCompleto);
    }

    public EstablecimientoResponseDTO guardar(EstablecimientoRequestDTO dto) {
        log.info("GUARDANDO ESTABLECIMIENTO: {}", dto.getNombre());
        Establecimiento e = new Establecimiento();
        e.setNombre(dto.getNombre());
        e.setDireccion(dto.getDireccion());
        Establecimiento guardado = establecimientoRepository.save(e);
        log.info("ESTABLECIMIENTO GUARDADO CON ID: {}", guardado.getId());
        return mapToDTO(guardado);
    }

    public void eliminarPorId(Long id) {
        log.info("ELIMINANDO ESTABLECIMIENTO CON ID: {}", id);
        establecimientoRepository.deleteById(id);
    }

    public List<Object> obtenerEntrenadores(Long establecimientoId) {
        log.info("OBTENIENDO ENTRENADORES DEL ESTABLECIMIENTO ID: {}", establecimientoId);
        return entrenadorClient.obtenerEntrenadeoresPorEstablecimiento(establecimientoId);
    }

    public List<Object> obtenerClientes(Long establecimientoId) {
        log.info("OBTENIENDO CLIENTES DEL ESTABLECIMIENTO ID: {}", establecimientoId);
        return clienteClient.obtenerClientesPorEstablecimiento(establecimientoId);
    }

    public void asignarEntrenador(Long establecimientoId, Long entrenadorId) {
        log.info("ASIGNANDO ENTRENADOR {} AL ESTABLECIMIENTO {}", entrenadorId, establecimientoId);
        establecimientoRepository.findById(establecimientoId)
                .orElseThrow(() -> new NoSuchElementException("Establecimiento no encontrado con ID: " + establecimientoId));
        entrenadorClient.verificarEntrenadorExiste(entrenadorId);
        entrenadorClient.asignarEstablecimientoAEntrenador(entrenadorId, establecimientoId);
        log.info("ENTRENADOR {} ASIGNADO AL ESTABLECIMIENTO {} CORRECTAMENTE", entrenadorId, establecimientoId);
    }

    public void crearEquipo(Long establecimientoId, EquipoRequestDTO dto) {
        log.info("CREANDO EQUIPO EN ESTABLECIMIENTO ID: {}", establecimientoId);
        establecimientoRepository.findById(establecimientoId)
                .orElseThrow(() -> new NoSuchElementException("Establecimiento no encontrado con ID: " + establecimientoId));
        equipoClient.crearEquipo(establecimientoId, dto);
        log.info("EQUIPO CREADO CORRECTAMENTE EN ESTABLECIMIENTO {}", establecimientoId);
    }
}