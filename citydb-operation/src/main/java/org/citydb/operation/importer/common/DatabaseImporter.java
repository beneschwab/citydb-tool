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

package org.citydb.operation.importer.common;

import org.citydb.core.file.FileLocator;
import org.citydb.database.adapter.DatabaseAdapter;
import org.citydb.database.geometry.GeometryException;
import org.citydb.database.schema.*;
import org.citydb.model.common.ExternalFile;
import org.citydb.model.common.Reference;
import org.citydb.model.geometry.Envelope;
import org.citydb.model.geometry.Geometry;
import org.citydb.model.geometry.Polygon;
import org.citydb.operation.importer.ImportException;
import org.citydb.operation.importer.ImportHelper;
import org.citydb.operation.importer.reference.CacheType;
import org.citydb.operation.importer.util.TableHelper;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class DatabaseImporter {
    protected final Table table;
    private final ImportHelper helper;
    protected final DatabaseAdapter adapter;
    protected final DataTypeHelper dataTypeHelper;
    protected final NamespaceHelper namespaceHelper;
    protected final ObjectClassHelper objectClassHelper;
    protected final TableHelper tableHelper;
    protected final PreparedStatement stmt;

    private int batchCounter;

    public DatabaseImporter(Table table, ImportHelper helper) throws SQLException {
        this.table = table;
        this.helper = helper;
        this.adapter = helper.getAdapter();
        this.dataTypeHelper = helper.getDataTypeHelper();
        this.namespaceHelper = helper.getNamespaceHelper();
        this.objectClassHelper = helper.getObjectClassHelper();
        this.tableHelper = helper.getTableHelper();
        stmt = helper.getConnection().prepareStatement(getInsertStatement());
    }

    protected abstract String getInsertStatement();

    protected long nextSequenceValue(Sequence sequence) throws SQLException {
        return helper.getSequenceValues().next(sequence);
    }

    protected void cacheTarget(CacheType type, String objectId, long id) {
        if (objectId != null) {
            helper.getOrCreateReferenceCache(type).putTarget(objectId, id);
        }
    }

    protected void cacheReference(CacheType type, Reference reference, long id) {
        if (reference != null) {
            helper.getOrCreateReferenceCache(type).putReference(reference, id);
        }
    }

    protected FileLocator getFileLocator(ExternalFile file) {
        return helper.getFileLocator(file);
    }

    protected Object getGeometry(Geometry<?> geometry, boolean force3D) throws ImportException {
        try {
            return geometry != null ? adapter.getGeometryAdapter().getGeometry(geometry, force3D) : null;
        } catch (GeometryException e) {
            throw new ImportException("Failed to convert geometry to database representation.", e);
        }
    }

    protected Object getGeometry(Geometry<?> geometry) throws ImportException {
        return getGeometry(geometry, true);
    }

    protected Object getEnvelope(Envelope envelope) throws ImportException {
        return envelope != null ? getGeometry(Polygon.of(envelope), true) : null;
    }

    protected void addBatch() throws SQLException {
        stmt.addBatch();
        if (++batchCounter == adapter.getSchemaAdapter().getMaximumBatchSize()) {
            for (Table table : tableHelper.getCommitOrder(table)) {
                for (DatabaseImporter importer : tableHelper.getImporters(table)) {
                    importer.executeBatch();
                }
            }
        }
    }

    public void executeBatch() throws SQLException {
        if (batchCounter > 0) {
            stmt.executeBatch();
            batchCounter = 0;
        }
    }

    public void close() throws SQLException {
        stmt.close();
    }
}
