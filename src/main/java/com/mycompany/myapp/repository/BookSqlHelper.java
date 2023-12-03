package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class BookSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("title", table, columnPrefix + "_title"));
        columns.add(Column.aliased("descripton", table, columnPrefix + "_descripton"));
        columns.add(Column.aliased("publication_date", table, columnPrefix + "_publication_date"));
        columns.add(Column.aliased("bookimage", table, columnPrefix + "_bookimage"));
        columns.add(Column.aliased("bookimage_content_type", table, columnPrefix + "_bookimage_content_type"));

        columns.add(Column.aliased("name_id", table, columnPrefix + "_name_id"));
        return columns;
    }
}
