package Establecimiento.Establecimiento.config;

import Establecimiento.Establecimiento.Model.Establecimiento;
import Establecimiento.Establecimiento.Repository.EstablecimientoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final EstablecimientoRepository establecimientoRepository;

    @Override
    public void run(String... args) {
        if (establecimientoRepository.count() > 0) {
            log.info(">>> DataInitializer: la BD ya tiene datos, se omite la carga inicial.");
            return;
        }

        log.info(">>> DataInitializer: BD vacía detectada, insertando datos de prueba...");

        establecimientoRepository.save(new Establecimiento(null, "FitChain Santiago", "Av. Providencia 1234, Santiago"));
        establecimientoRepository.save(new Establecimiento(null, "FitChain Valparaíso", "Av. Brasil 567, Valparaíso"));

        log.info(">>> DataInitializer: {} establecimientos insertados.", establecimientoRepository.count());
    }
}