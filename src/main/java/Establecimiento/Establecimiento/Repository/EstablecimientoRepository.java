package Establecimiento.Establecimiento.Repository;

import Establecimiento.Establecimiento.Model.Establecimiento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstablecimientoRepository extends JpaRepository<Establecimiento, Long> {
}