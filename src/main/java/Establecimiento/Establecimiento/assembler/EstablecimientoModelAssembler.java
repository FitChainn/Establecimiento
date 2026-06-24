package Establecimiento.Establecimiento.assembler;

import Establecimiento.Establecimiento.Controller.EstablecimientoController;
import Establecimiento.Establecimiento.dto.EstablecimientoResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EstablecimientoModelAssembler implements RepresentationModelAssembler<EstablecimientoResponseDTO, EntityModel<EstablecimientoResponseDTO>> {
    @Override
    public EntityModel<EstablecimientoResponseDTO> toModel(EstablecimientoResponseDTO dto) {
        return EntityModel.of(
                dto,
                linkTo(methodOn(EstablecimientoController.class).obtenerPorId(dto.getId())).withSelfRel(),
                linkTo(methodOn(EstablecimientoController.class).eliminar(dto.getId())).withRel("delete")
        );
    }
}
