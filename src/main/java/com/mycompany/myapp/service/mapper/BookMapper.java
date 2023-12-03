package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Author;
import com.mycompany.myapp.domain.Book;
import com.mycompany.myapp.service.dto.AuthorDTO;
import com.mycompany.myapp.service.dto.BookDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Book} and its DTO {@link BookDTO}.
 */
@Mapper(componentModel = "spring")
public interface BookMapper extends EntityMapper<BookDTO, Book> {
    @Mapping(target = "name", source = "name", qualifiedByName = "authorId")
    BookDTO toDto(Book s);

    @Named("authorId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AuthorDTO toDtoAuthorId(Author author);
}
