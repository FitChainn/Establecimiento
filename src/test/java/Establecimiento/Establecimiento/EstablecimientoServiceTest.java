package Establecimiento.Establecimiento;

import Establecimiento.Establecimiento.Model.Establecimiento;
import Establecimiento.Establecimiento.Repository.EstablecimientoRepository;
import Establecimiento.Establecimiento.Service.EstablecimientoService;
import Establecimiento.Establecimiento.WebClient.ClienteClient;
import Establecimiento.Establecimiento.WebClient.EntrenadorClient;
import Establecimiento.Establecimiento.WebClient.EquipoClient;
import Establecimiento.Establecimiento.dto.EquipoRequestDTO;
import Establecimiento.Establecimiento.dto.EstablecimientoRequestDTO;
import Establecimiento.Establecimiento.dto.EstablecimientoResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PRUEBAS UNITARIAS DEL SERVICE DE ESTABLECIMIENTO")
public class EstablecimientoServiceTest {

    @Mock
    private EstablecimientoRepository establecimientoRepository;

    @Mock
    private EntrenadorClient entrenadorClient;

    @Mock
    private ClienteClient clienteClient;

    @Mock
    private EquipoClient equipoClient;

    @InjectMocks
    private EstablecimientoService establecimientoService;

    private Establecimiento establecimiento;
    private EstablecimientoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        establecimiento = new Establecimiento(1L, "GYM CENTRAL", "CALLE VALPARAISO");
        requestDTO = new EstablecimientoRequestDTO("GYM POROTOS", "CALLE ANDRES BELLO");
    }

    @Test
    @DisplayName("DEBE RETORNAR TODOS LOS ESTABLECIMIENTOS")
    void shouldReturnTodosLosEstablecimientos() {
        when(establecimientoRepository.findAll()).thenReturn(List.of(establecimiento));
        when(entrenadorClient.obtenerEntrenadeoresPorEstablecimiento(1L)).thenReturn(Collections.emptyList());
        when(clienteClient.obtenerClientesPorEstablecimiento(1L)).thenReturn(Collections.emptyList());
        when(equipoClient.obtenerResumenEquipos(1L)).thenReturn(Map.of());

        List<EstablecimientoResponseDTO> resultado = establecimientoService.obtenerTodos();

        assertFalse(resultado.isEmpty());
        assertEquals("GYM CENTRAL", resultado.get(0).getNombre());
        verify(establecimientoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("DEBE RETORNAR UN ESTABLECIMIENTO POR ID")
    void shouldReturnEstablecimientoById() {
        when(establecimientoRepository.findById(1L)).thenReturn(Optional.of(establecimiento));
        when(entrenadorClient.obtenerEntrenadeoresPorEstablecimiento(1L)).thenReturn(Collections.emptyList());
        when(clienteClient.obtenerClientesPorEstablecimiento(1L)).thenReturn(Collections.emptyList());
        when(equipoClient.obtenerResumenEquipos(1L)).thenReturn(Map.of());

        Optional<EstablecimientoResponseDTO> resultado = establecimientoService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("GYM CENTRAL", resultado.get().getNombre());
        verify(establecimientoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("DEBE GUARDAR UN ESTABLECIMIENTO")
    void shouldGuardarEstablecimiento() {
        when(establecimientoRepository.save(any(Establecimiento.class))).thenReturn(establecimiento);

        EstablecimientoResponseDTO resultado = establecimientoService.guardar(requestDTO);

        assertNotNull(resultado);
        assertEquals("GYM CENTRAL", resultado.getNombre());
        assertEquals("CALLE VALPARAISO", resultado.getDireccion());
        verify(establecimientoRepository, times(1)).save(any(Establecimiento.class));
    }

    @Test
    @DisplayName("DEBE ELIMINAR UN ESTABLECIMIENTO POR ID")
    void shouldEliminarEstablecimiento() {
        doNothing().when(establecimientoRepository).deleteById(1L);

        establecimientoService.eliminarPorId(1L);

        verify(establecimientoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("DEBE OBTENER ENTRENADORES POR ESTABLECIMIENTO")
    void shouldObtenerEntrenadores() {
        when(entrenadorClient.obtenerEntrenadeoresPorEstablecimiento(1L)).thenReturn(List.of("Entrenador 1"));

        List<Object> resultado = establecimientoService.obtenerEntrenadores(1L);

        assertEquals(1, resultado.size());
        verify(entrenadorClient, times(1)).obtenerEntrenadeoresPorEstablecimiento(1L);
    }

    @Test
    @DisplayName("DEBE OBTENER CLIENTES POR ESTABLECIMIENTO")
    void shouldObtenerClientes() {
        when(clienteClient.obtenerClientesPorEstablecimiento(1L)).thenReturn(List.of("Cliente 1"));

        List<Object> resultado = establecimientoService.obtenerClientes(1L);

        assertEquals(1, resultado.size());
        verify(clienteClient, times(1)).obtenerClientesPorEstablecimiento(1L);
    }

    @Test
    @DisplayName("DEBE ASIGNAR UN ENTRENADOR A UN ESTABLECIMIENTO")
    void shouldAsignarEntrenador() {
        Long entrenadorId = 2L;
        when(establecimientoRepository.findById(1L)).thenReturn(Optional.of(establecimiento));
        doNothing().when(entrenadorClient).verificarEntrenadorExiste(entrenadorId);
        doNothing().when(entrenadorClient).asignarEstablecimientoAEntrenador(entrenadorId, 1L);

        assertDoesNotThrow(() -> establecimientoService.asignarEntrenador(1L, entrenadorId));

        verify(establecimientoRepository, times(1)).findById(1L);
        verify(entrenadorClient, times(1)).verificarEntrenadorExiste(entrenadorId);
        verify(entrenadorClient, times(1)).asignarEstablecimientoAEntrenador(entrenadorId, 1L);
    }

    @Test
    @DisplayName("DEBE LANZAR EXCEPCION SI ESTABLECIMIENTO NO EXISTE AL ASIGNAR ENTRENADOR")
    void shouldThrowWhenAsignarEntrenadorYEstablecimientoNoExiste() {
        when(establecimientoRepository.findById(99L)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(NoSuchElementException.class,
                () -> establecimientoService.asignarEntrenador(99L, 2L));

        assertEquals("Establecimiento no encontrado con ID: 99", ex.getMessage());
        verify(entrenadorClient, never()).verificarEntrenadorExiste(anyLong());
    }

    @Test
    @DisplayName("DEBE CREAR UN EQUIPO EN UN ESTABLECIMIENTO")
    void shouldCrearEquipo() {
        EquipoRequestDTO equipoDTO = new EquipoRequestDTO("Cinta", "BH", LocalDate.now(), "ACTIVO");
        when(establecimientoRepository.findById(1L)).thenReturn(Optional.of(establecimiento));
        doNothing().when(equipoClient).crearEquipo(1L, equipoDTO);

        assertDoesNotThrow(() -> establecimientoService.crearEquipo(1L, equipoDTO));

        verify(establecimientoRepository, times(1)).findById(1L);
        verify(equipoClient, times(1)).crearEquipo(1L, equipoDTO);
    }

    @Test
    @DisplayName("DEBE LANZAR EXCEPCION SI ESTABLECIMIENTO NO EXISTE AL CREAR EQUIPO")
    void shouldThrowWhenCrearEquipoYEstablecimientoNoExiste() {
        EquipoRequestDTO equipoDTO = new EquipoRequestDTO("Cinta", "BH", LocalDate.now(), "ACTIVO");
        when(establecimientoRepository.findById(99L)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(NoSuchElementException.class,
                () -> establecimientoService.crearEquipo(99L, equipoDTO));

        assertEquals("Establecimiento no encontrado con ID: 99", ex.getMessage());
        verify(equipoClient, never()).crearEquipo(anyLong(), any());
    }
}