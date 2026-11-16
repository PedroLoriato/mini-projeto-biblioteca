package pedro.biblioteca.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import pedro.biblioteca.domain.LivroDomain;
import pedro.biblioteca.dto.livro.LivroRequest;
import pedro.biblioteca.dto.livro.LivroResponse;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LivroMapper {

    @Mapping(target = "id", ignore = true)
    LivroDomain toDomain(LivroRequest dto);

    LivroResponse toResponse(LivroDomain domain);
}