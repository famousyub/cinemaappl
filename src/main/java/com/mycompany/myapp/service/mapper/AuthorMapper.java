package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Author;
import com.mycompany.myapp.service.dto.AuthorDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Author} and its DTO {@link AuthorDTO}.
 */
@Mapper(componentModel = "spring")
public interface AuthorMapper extends EntityMapper<AuthorDTO, Author> {}
