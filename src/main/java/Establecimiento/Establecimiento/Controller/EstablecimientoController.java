package Establecimiento.Establecimiento.Controller;

import Establecimiento.Establecimiento.Service.EstablecimientoService;
import Establecimiento.Establecimiento.dto.EstablecimientoRequestDTO;
import Establecimiento.Establecimiento.dto.EstablecimientoResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/establecimientos")
public class EstablecimientoController {

    @Autowired
    private EstablecimientoService establecimientoService;

    @GetMapping
    public ResponseEntity<List<EstablecimientoResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(establecimientoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstablecimientoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return establecimientoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/entrenadores")
    public ResponseEntity<List<Object>> obtenerEntrenadores(@PathVariable Long id) {
        return ResponseEntity.ok(establecimientoService.obtenerEntrenadores(id));
    }

    @GetMapping("/{id}/clientes")
    public ResponseEntity<List<Object>> obtenerClientes(@PathVariable Long id) {
        return ResponseEntity.ok(establecimientoService.obtenerClientes(id));
    }

    @PostMapping
    public ResponseEntity<EstablecimientoResponseDTO> crearEstablecimiento(@RequestBody EstablecimientoRequestDTO dto) {
        return ResponseEntity.status(201).body(establecimientoService.guardar(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (establecimientoService.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        establecimientoService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }
}