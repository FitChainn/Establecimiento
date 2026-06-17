package Establecimiento.Establecimiento;

import Establecimiento.Establecimiento.Controller.EstablecimientoController;
import Establecimiento.Establecimiento.Service.EstablecimientoService;
import Establecimiento.Establecimiento.config.SecurityConfig;
import Establecimiento.Establecimiento.dto.EstablecimientoRequestDTO;
import Establecimiento.Establecimiento.dto.EstablecimientoResponseDTO;
import Establecimiento.Establecimiento.filter.RolHeaderFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EstablecimientoController.class)
@Import({SecurityConfig.class, RolHeaderFilter.class})
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
        eResponse = new EstablecimientoResponseDTO(1L, "GYM CENTRAL", "CALLE VALPARAISO", Collections.emptyList(), Collections.emptyList(), Collections.emptyMap());
        eRequest = new EstablecimientoRequestDTO("GYM POROTOS", "CALLE ANDRES BELLO");
    }

    @Test
    void Get_obtenerTodos() throws Exception {
        when(establecimientoService.obtenerTodos()).thenReturn(List.of(eResponse));

        mockMvc.perform(get("/v1/establecimientos")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("GYM CENTRAL"));
    }

    @Test
    void Post_crear201() throws Exception {
        when(establecimientoService.guardar(any(EstablecimientoRequestDTO.class))).thenReturn(eResponse);

        mockMvc.perform(post("/v1/establecimientos")
                        .header("X-User-Rol", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void Get_obtenerPorId() throws Exception {
        when(establecimientoService.obtenerPorId(1L)).thenReturn(Optional.of(eResponse));

        mockMvc.perform(get("/v1/establecimientos/1")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("GYM CENTRAL"));
    }

    @Test
    void Delete_eliminar() throws Exception {
        when(establecimientoService.obtenerPorId(1L)).thenReturn(Optional.of(eResponse));

        mockMvc.perform(delete("/v1/establecimientos/1")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isNoContent());
    }
}
