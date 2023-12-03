package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class AuthorSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("nameauthor", table, columnPrefix + "_nameauthor"));
        columns.add(Column.aliased("birth_date", table, columnPrefix + "_birth_date"));
        columns.add(Column.aliased("description", table, columnPrefix + "_description"));

        return columns;
    }
}
