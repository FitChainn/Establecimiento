package Establecimiento.Establecimiento;

import Establecimiento.Establecimiento.Model.Establecimiento;
import Establecimiento.Establecimiento.Repository.EstablecimientoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("PRUEBAS UNITARIAS DEL REPOSITORY DE ESTABLECIMIENTO")
public class EstablecimientoRepositoryTest {

    @Autowired
    private EstablecimientoRepository repo;

    @Autowired
    private TestEntityManager em;

    private Establecimiento crearEstablecimiento(String nombre, String direccion) {
        Establecimiento e = new Establecimiento();
        e.setNombre(nombre);
        e.setDireccion(direccion);
        return em.persistAndFlush(e);
    }

    @Test
    @DisplayName("DEBE ENCONTRAR UN ESTABLECIMIENTO POR ID")
    void findById_ShouldReturnEstablecimiento() {
        Establecimiento e = crearEstablecimiento("GYM PACIFICO", "CALLE PRAT");

        Optional<Establecimiento> result = repo.findById(e.getId());

        assertTrue(result.isPresent());
        assertEquals("GYM PACIFICO", result.get().getNombre());
    }

    @Test
    @DisplayName("DEBE RETORNAR VACIO SI ESTABLECIMIENTO NO EXISTE")
    void findById_ShouldReturnEmpty() {
        Optional<Establecimiento> result = repo.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("DEBE ENCONTRAR TODOS LOS ESTABLECIMIENTOS")
    void findAll_ShouldReturnAllEstablecimientos() {
        crearEstablecimiento("GYM PACIFICO", "CALLE PRAT");
        crearEstablecimiento("GYM ENERGY", "AVENIDA LIBERTAD");

        List<Establecimiento> lista = repo.findAll();

        assertFalse(lista.isEmpty());
        assertTrue(lista.size() >= 2);
    }

    @Test
    @DisplayName("DEBE GUARDAR UN ESTABLECIMIENTO")
    void save_ShouldPersistEstablecimiento() {
        Establecimiento e = new Establecimiento();
        e.setNombre("GYM CENTRAL");
        e.setDireccion("CALLE VALPARAISO");

        Establecimiento saved = repo.save(e);

        assertNotNull(saved.getId());
        assertEquals("GYM CENTRAL", saved.getNombre());
    }

    @Test
    @DisplayName("DEBE ELIMINAR UN ESTABLECIMIENTO")
    void delete_ShouldRemoveEstablecimiento() {
        Establecimiento e = crearEstablecimiento("GYM PACIFICO", "CALLE PRAT");
        Long id = e.getId();

        repo.deleteById(id);
        em.flush();

        assertFalse(repo.findById(id).isPresent());
    }
}