package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Book;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Book}, with proper type conversions.
 */
@Service
public class BookRowMapper implements BiFunction<Row, String, Book> {

    private final ColumnConverter converter;

    public BookRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Book} stored in the database.
     */
    @Override
    public Book apply(Row row, String prefix) {
        Book entity = new Book();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setDescripton(converter.fromRow(row, prefix + "_descripton", String.class));
        entity.setPublicationDate(converter.fromRow(row, prefix + "_publication_date", LocalDate.class));
        entity.setBookimageContentType(converter.fromRow(row, prefix + "_bookimage_content_type", String.class));
        entity.setBookimage(converter.fromRow(row, prefix + "_bookimage", byte[].class));
        entity.setNameId(converter.fromRow(row, prefix + "_name_id", Long.class));
        return entity;
    }
}
