package Establecimiento.Establecimiento;

import Establecimiento.Establecimiento.Controller.EstablecimientoController;
import Establecimiento.Establecimiento.Service.EstablecimientoService;
import Establecimiento.Establecimiento.config.SecurityConfig;
import Establecimiento.Establecimiento.dto.EquipoRequestDTO;
import Establecimiento.Establecimiento.dto.EstablecimientoRequestDTO;
import Establecimiento.Establecimiento.dto.EstablecimientoResponseDTO;
import Establecimiento.Establecimiento.filter.RolHeaderFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EstablecimientoController.class)
@Import({SecurityConfig.class, RolHeaderFilter.class})
@DisplayName("PRUEBAS UNITARIAS DEL CONTROLLER DE ESTABLECIMIENTO")
public class EstablecimientoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EstablecimientoService establecimientoService;

    @Autowired
    private ObjectMapper objectMapper;

    private EstablecimientoResponseDTO eResponse;
    private EstablecimientoRequestDTO eRequest;

    @BeforeEach
    void setUp() {
        eResponse = new EstablecimientoResponseDTO(1L, "GYM CENTRAL", "CALLE VALPARAISO", Collections.emptyList(), Collections.emptyList(), Map.of());
        eRequest = new EstablecimientoRequestDTO("GYM POROTOS", "CALLE ANDRES BELLO");
    }

    @Test
    @DisplayName("DEBE RETORNAR TODOS LOS ESTABLECIMIENTOS")
    void GET_obtenerTodos() throws Exception {
        when(establecimientoService.obtenerTodos()).thenReturn(List.of(eResponse));

        mockMvc.perform(get("/v1/establecimientos")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("GYM CENTRAL"));
    }

    @Test
    @DisplayName("DEBE CREAR UN ESTABLECIMIENTO (201)")
    void POST_crear201() throws Exception {
        when(establecimientoService.guardar(any(EstablecimientoRequestDTO.class))).thenReturn(eResponse);

        mockMvc.perform(post("/v1/establecimientos")
                        .header("X-User-Rol", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("GYM CENTRAL"));
    }

    @Test
    @DisplayName("DEBE RETORNAR ERROR 400 AL CREAR ESTABLECIMIENTO CON DATOS INVALIDOS")
    void POST_validation_crear() throws Exception {
        EstablecimientoRequestDTO reqInvalido = new EstablecimientoRequestDTO();

        mockMvc.perform(post("/v1/establecimientos")
                        .header("X-User-Rol", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DEBE OBTENER UN ESTABLECIMIENTO POR ID")
    void GET_obtenerPorId() throws Exception {
        when(establecimientoService.obtenerPorId(1L)).thenReturn(Optional.of(eResponse));

        mockMvc.perform(get("/v1/establecimientos/1")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("GYM CENTRAL"));
    }

    @Test
    @DisplayName("DEBE RETORNAR 404 SI ESTABLECIMIENTO NO EXISTE")
    void GET_obtenerIdNotFound() throws Exception {
        when(establecimientoService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/v1/establecimientos/99")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DEBE OBTENER ENTRENADORES DEL ESTABLECIMIENTO")
    void GET_obtenerEntrenadores() throws Exception {
        when(establecimientoService.obtenerEntrenadores(1L)).thenReturn(List.of("Entrenador 1", "Entrenador 2"));

        mockMvc.perform(get("/v1/establecimientos/1/entrenadores")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("DEBE OBTENER CLIENTES DEL ESTABLECIMIENTO")
    void GET_obtenerClientes() throws Exception {
        when(establecimientoService.obtenerClientes(1L)).thenReturn(List.of("Cliente 1"));

        mockMvc.perform(get("/v1/establecimientos/1/clientes")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("DEBE ASIGNAR UN ENTRENADOR A UN ESTABLECIMIENTO")
    void PUT_asignarEntrenador() throws Exception {
        doNothing().when(establecimientoService).asignarEntrenador(1L, 2L);

        mockMvc.perform(put("/v1/establecimientos/1/entrenador/2")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DEBE CREAR UN EQUIPO EN EL ESTABLECIMIENTO")
    void POST_crearEquipo() throws Exception {
        EquipoRequestDTO equipoReq = new EquipoRequestDTO("Cinta", "BH", LocalDate.now(), "ACTIVO");
        doNothing().when(establecimientoService).crearEquipo(eq(1L), any(EquipoRequestDTO.class));

        mockMvc.perform(post("/v1/establecimientos/1/equipo")
                        .header("X-User-Rol", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(equipoReq)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("DEBE ELIMINAR UN ESTABLECIMIENTO")
    void DELETE_eliminar() throws Exception {
        when(establecimientoService.obtenerPorId(1L)).thenReturn(Optional.of(eResponse));
        doNothing().when(establecimientoService).eliminarPorId(1L);

        mockMvc.perform(delete("/v1/establecimientos/1")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isNoContent());

        verify(establecimientoService, times(1)).eliminarPorId(1L);
    }
}