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

package org.citydb.io.citygml.adapter.gml;

import org.citydb.io.citygml.builder.ModelBuildException;
import org.citydb.io.citygml.builder.ModelBuilder;
import org.citydb.io.citygml.reader.ModelBuilderHelper;
import org.citydb.io.citygml.serializer.ModelSerializeException;
import org.citydb.io.citygml.serializer.ModelSerializer;
import org.citydb.io.citygml.writer.ModelSerializerHelper;
import org.citydb.model.property.Attribute;
import org.citydb.model.property.DataType;
import org.xmlobjects.gml.model.basictypes.Code;

public class CodeAdapter implements ModelBuilder<Code, Attribute>, ModelSerializer<Attribute, Code> {

    @Override
    public void build(Code source, Attribute target, ModelBuilderHelper helper) throws ModelBuildException {
        target.setStringValue(source.getValue())
                .setCodeSpace(source.getCodeSpace())
                .setDataType(DataType.CODE);
    }

    @Override
    public Code createObject(Attribute source) throws ModelSerializeException {
        return new Code();
    }

    @Override
    public void serialize(Attribute source, Code target, ModelSerializerHelper helper) throws ModelSerializeException {
        source.getStringValue().ifPresent(target::setValue);
        source.getCodeSpace().ifPresent(target::setCodeSpace);
    }
}
