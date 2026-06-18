package Establecimiento.Establecimiento.Controller;

import Establecimiento.Establecimiento.Service.EstablecimientoService;
import Establecimiento.Establecimiento.dto.EquipoRequestDTO;
import Establecimiento.Establecimiento.dto.EstablecimientoRequestDTO;
import Establecimiento.Establecimiento.dto.EstablecimientoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "ESTABLECIMIENTOS", description = "GESTION DE LOS ESTABLECIMIENTOS")
@RestController
@RequestMapping("/v1/establecimientos")
public class EstablecimientoController {

    @Autowired
    private EstablecimientoService establecimientoService;

    @Operation(summary = "OBTENER TODOS LOS ESTABLECIMIENTOS", description = "Retorna la lista de todos los establecimientos. Acceso: ADMIN, ENTRENADOR, CLIENTE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR', 'CLIENTE')")
    @GetMapping
    public ResponseEntity<List<EstablecimientoResponseDTO>> obtenerTodos() {
        log.info("GET /v1/establecimientos - LISTAR TODOS");
        return ResponseEntity.ok(establecimientoService.obtenerTodos());
    }

    @Operation(summary = "OBTENER ESTABLECIMIENTO POR ID", description = "Retorna un establecimiento específico por su ID. Acceso: ADMIN, ENTRENADOR, CLIENTE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Establecimiento encontrado"),
            @ApiResponse(responseCode = "404", description = "Establecimiento no encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR', 'CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<EstablecimientoResponseDTO> obtenerPorId(@PathVariable Long id) {
        log.info("GET /v1/establecimientos/{} - BUSCAR POR ID", id);
        return establecimientoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "OBTENER ENTRENADORES DEL ESTABLECIMIENTO", description = "Retorna la lista de entrenadores asociados a un establecimiento. Acceso: ADMIN, ENTRENADOR")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Establecimiento no encontrado"),
            @ApiResponse(responseCode = "503", description = "MICROSERVICIO NO DISPONIBLE")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR')")
    @GetMapping("/{id}/entrenadores")
    public ResponseEntity<List<Object>> obtenerEntrenadores(@PathVariable Long id) {
        log.info("GET /v1/establecimientos/{}/entrenadores - LISTAR ENTRENADORES", id);
        return ResponseEntity.ok(establecimientoService.obtenerEntrenadores(id));
    }

    @Operation(summary = "OBTENER CLIENTES DEL ESTABLECIMIENTO", description = "Retorna la lista de clientes asociados a un establecimiento. Acceso: ADMIN, ENTRENADOR")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Establecimiento no encontrado"),
            @ApiResponse(responseCode = "503", description = "MICROSERVICIO NO DISPONIBLE")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR')")
    @GetMapping("/{id}/clientes")
    public ResponseEntity<List<Object>> obtenerClientes(@PathVariable Long id) {
        log.info("GET /v1/establecimientos/{}/clientes - LISTAR CLIENTES", id);
        return ResponseEntity.ok(establecimientoService.obtenerClientes(id));
    }

    @Operation(summary = "CREAR ESTABLECIMIENTO", description = "Crea un nuevo establecimiento. Acceso: ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Establecimiento creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EstablecimientoResponseDTO> crearEstablecimiento(
            @Valid @RequestBody EstablecimientoRequestDTO dto) {
        log.info("POST /v1/establecimientos - CREAR ESTABLECIMIENTO nombre={}", dto.getNombre());
        return ResponseEntity.status(201).body(establecimientoService.guardar(dto));
    }

    @Operation(summary = "ELIMINAR ESTABLECIMIENTO", description = "Elimina un establecimiento por su ID. Acceso: ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Establecimiento eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Establecimiento no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        log.info("DELETE /v1/establecimientos/{} - ELIMINAR ESTABLECIMIENTO", id);
        if (establecimientoService.obtenerPorId(id).isEmpty()) return ResponseEntity.notFound().build();
        establecimientoService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "ASIGNAR ENTRENADOR A ESTABLECIMIENTO", description = "Asigna un entrenador existente a un establecimiento. Acceso: ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entrenador asignado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Establecimiento o entrenador no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{establecimientoId}/entrenador/{entrenadorId}")
    public ResponseEntity<?> asignarEntrenador(
            @PathVariable Long establecimientoId,
            @PathVariable Long entrenadorId) {
        log.info("PUT /v1/establecimientos/{}/entrenador/{} - ASIGNAR ENTRENADOR", establecimientoId, entrenadorId);
        establecimientoService.asignarEntrenador(establecimientoId, entrenadorId);
        return ResponseEntity.ok("Entrenador " + entrenadorId + " asignado al establecimiento " + establecimientoId);
    }

    @Operation(summary = "CREAR EQUIPO EN ESTABLECIMIENTO", description = "Crea un nuevo equipo asociado a un establecimiento. Acceso: ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Equipo creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Establecimiento no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{establecimientoId}/equipo")
    public ResponseEntity<?> crearEquipo(
            @PathVariable Long establecimientoId,
            @Valid @RequestBody EquipoRequestDTO dto) {
        log.info("POST /v1/establecimientos/{}/equipo - CREAR EQUIPO", establecimientoId);
        establecimientoService.crearEquipo(establecimientoId, dto);
        return ResponseEntity.status(201).body("Equipo creado correctamente en el establecimiento " + establecimientoId);
    }
}