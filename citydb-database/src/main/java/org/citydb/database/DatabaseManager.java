/*
 * citydb-tool - Command-line tool for the 3D City Database
 * https://www.3dcitydb.org/
 *
 * Copyright 2022-2023
 * Virtual City Systems, Germany
 * https://vc.systems/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.citydb.database;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.citydb.database.adapter.DatabaseAdapter;
import org.citydb.database.adapter.DatabaseAdapterException;
import org.citydb.database.adapter.DatabaseAdapterManager;
import org.citydb.database.connection.ConnectionDetails;

import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Consumer;

public class DatabaseManager {
    private DatabaseAdapter adapter;
    private DataSource dataSource;

    private DatabaseManager() {
    }

    public static DatabaseManager newInstance() {
        return new DatabaseManager();
    }

    public void connect(ConnectionDetails connectionDetails, DatabaseAdapterManager manager) throws DatabaseException, SQLException {
        Objects.requireNonNull(connectionDetails, "The connection details must not be null.");

        adapter = manager.getAdapterForDatabase(connectionDetails.getDatabaseName());
        if (adapter == null) {
            throw new DatabaseException("No database adapter available for the database '" +
                    connectionDetails.getDatabaseName() + "'.");
        }

        PoolProperties properties = new PoolProperties();
        properties.setDriverClassName(adapter.getDriverClass().getName());
        properties.setUsername(connectionDetails.getUser());
        properties.setPassword(connectionDetails.getPassword());
        properties.setUrl(adapter.getConnectionString(connectionDetails.getHost(),
                connectionDetails.getPort(),
                connectionDetails.getDatabase()));

        properties.setInitialSize(0);
        properties.setDefaultAutoCommit(true);

        dataSource = new DataSource(properties);
        dataSource.setLoginTimeout(connectionDetails.getPoolOptions().getLoginTimeout());
        dataSource.createPool();

        adapter.initialize(Pool.newInstance(this), connectionDetails);
    }

    public void connect(ConnectionDetails connectionDetails) throws DatabaseException, SQLException {
        try {
            connect(connectionDetails, DatabaseAdapterManager.newInstance().load());
        } catch (DatabaseAdapterException e) {
            throw new DatabaseException("Failed to load database adapters.", e);
        }
    }

    public boolean isConnected() {
        return dataSource != null
                && dataSource.getPool() != null
                && !dataSource.getPool().isClosed();
    }

    public void disconnect() {
        dataSource.close(true);
        dataSource = null;
    }

    public DatabaseAdapter getAdapter() {
        return adapter;
    }

    DataSource getDataSource() {
        return dataSource;
    }

    public void printDatabaseMetadata(Consumer<String> consumer) {
        if (isConnected()) {
            consumer.accept("3D City Database: " + adapter.getDatabaseMetadata().getVersion());
            consumer.accept("DBMS: " + adapter.getDatabaseMetadata().getVendorProductName() + " " +
                    adapter.getDatabaseMetadata().getVendorProductVersion());
            consumer.accept("Connection: " + adapter.getConnectionDetails().toConnectString());
            consumer.accept("Schema: " + adapter.getConnectionDetails().getSchema());
            consumer.accept("SRID: " + adapter.getDatabaseMetadata().getSpatialReference().getSRID());
            consumer.accept("SRS name: " + adapter.getDatabaseMetadata().getSpatialReference().getName());
            consumer.accept("SRS URI: " + adapter.getDatabaseMetadata().getSpatialReference().getURI());
        }
    }
}
