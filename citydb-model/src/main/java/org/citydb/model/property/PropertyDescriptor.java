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

package org.citydb.model.property;

import org.citydb.model.common.DatabaseDescriptor;

public class PropertyDescriptor extends DatabaseDescriptor {
    private final long featureId;
    private long parentId;
    private long rootId;

    private PropertyDescriptor(long id, long featureId) {
        super(id);
        this.featureId = featureId;
    }

    public static PropertyDescriptor of(long id, long featureId) {
        return new PropertyDescriptor(id, featureId);
    }

    public long getFeatureId() {
        return featureId;
    }

    public PropertyDescriptor setParentId(long parentId) {
        this.parentId = parentId;
        return this;
    }

    public long getParentId() {
        return parentId;
    }

    public PropertyDescriptor setRootId(long rootId) {
        this.rootId = rootId;
        return this;
    }

    public long getRootId() {
        return rootId;
    }
}
