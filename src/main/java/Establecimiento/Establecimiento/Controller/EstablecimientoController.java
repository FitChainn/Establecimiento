package Establecimiento.Establecimiento.Controller;

import Establecimiento.Establecimiento.Service.EstablecimientoService;
import Establecimiento.Establecimiento.dto.EquipoRequestDTO;
import Establecimiento.Establecimiento.dto.EstablecimientoRequestDTO;
import Establecimiento.Establecimiento.dto.EstablecimientoResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/establecimientos")
public class EstablecimientoController {

    @Autowired
    private EstablecimientoService establecimientoService;

    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR', 'CLIENTE')")
    @GetMapping
    public ResponseEntity<List<EstablecimientoResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(establecimientoService.obtenerTodos());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR', 'CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<EstablecimientoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return establecimientoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR')")
    @GetMapping("/{id}/entrenadores")
    public ResponseEntity<List<Object>> obtenerEntrenadores(@PathVariable Long id) {
        return ResponseEntity.ok(establecimientoService.obtenerEntrenadores(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR')")
    @GetMapping("/{id}/clientes")
    public ResponseEntity<List<Object>> obtenerClientes(@PathVariable Long id) {
        return ResponseEntity.ok(establecimientoService.obtenerClientes(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EstablecimientoResponseDTO> crearEstablecimiento(
            @Valid @RequestBody EstablecimientoRequestDTO dto) {
        return ResponseEntity.status(201).body(establecimientoService.guardar(dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (establecimientoService.obtenerPorId(id).isEmpty()) return ResponseEntity.notFound().build();
        establecimientoService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{establecimientoId}/entrenador/{entrenadorId}")
    public ResponseEntity<?> asignarEntrenador(
            @PathVariable Long establecimientoId,
            @PathVariable Long entrenadorId) {
        establecimientoService.asignarEntrenador(establecimientoId, entrenadorId);
        return ResponseEntity.ok("Entrenador " + entrenadorId + " asignado al establecimiento " + establecimientoId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{establecimientoId}/equipo")
    public ResponseEntity<?> crearEquipo(
            @PathVariable Long establecimientoId,
            @Valid @RequestBody EquipoRequestDTO dto) {
        establecimientoService.crearEquipo(establecimientoId, dto);
        return ResponseEntity.status(201).body("Equipo creado correctamente en el establecimiento " + establecimientoId);
    }
}