package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Author;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Author}, with proper type conversions.
 */
@Service
public class AuthorRowMapper implements BiFunction<Row, String, Author> {

    private final ColumnConverter converter;

    public AuthorRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Author} stored in the database.
     */
    @Override
    public Author apply(Row row, String prefix) {
        Author entity = new Author();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNameauthor(converter.fromRow(row, prefix + "_nameauthor", String.class));
        entity.setBirthDate(converter.fromRow(row, prefix + "_birth_date", LocalDate.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        return entity;
    }
}
