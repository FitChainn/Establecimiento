package Establecimiento.Establecimiento.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstablecimientoResponseDTO {
    private Long id;
    private String nombre;
    private String direccion;
    private Object entrenadores;
    private Object clientes;
}