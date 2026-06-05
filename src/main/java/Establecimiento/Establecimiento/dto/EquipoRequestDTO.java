package Establecimiento.Establecimiento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipoRequestDTO {

    @NotBlank(message = "El tipo de máquina no puede estar vacío")
    private String tipoMaquina;

    @NotBlank(message = "La marca no puede estar vacía")
    private String marca;

    @NotNull(message = "La fecha de compra es obligatoria")
    private LocalDate fechaCompra;

    @NotBlank(message = "El estado no puede estar vacío")
    private String estado;
    // Sin establecimientoId — lo agrega Establecimiento internamente
}