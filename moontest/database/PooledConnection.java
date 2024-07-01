package org.moonstudio.moontest.database;

import java.sql.Connection;

public class PooledConnection implements AutoCloseable {
    private final Connection connection;
    private final SQLiteDatabase externalObject;

    public PooledConnection(Connection connection, SQLiteDatabase externalObject) {
        this.connection = connection;
        this.externalObject = externalObject;
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public void close() {
        externalObject.releaseConnection(connection);
    }
}
