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

package org.citydb.model.common;

import java.io.Serializable;
import java.util.Objects;

public class Name implements Serializable {
    private final String localName;
    private final String namespace;

    private Name(String localName, String namespace) {
        this.localName = Objects.requireNonNull(localName, "The local name must not be null.");
        this.namespace = Namespaces.ensureNonNull(namespace);
    }

    public static Name of(String localName, String namespace) {
        return new Name(localName, namespace);
    }

    public static Name of(String localName) {
        return new Name(localName, Namespaces.EMPTY_NAMESPACE);
    }

    public String getLocalName() {
        return localName;
    }

    public String getNamespace() {
        return namespace;
    }

    @Override
    public int hashCode() {
        return Objects.hash(localName, namespace);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof Name)) {
            return false;
        } else {
            Name other = (Name) obj;
            return localName.equals(other.localName) && namespace.equals(other.namespace);
        }
    }
}
