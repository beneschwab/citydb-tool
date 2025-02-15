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

package org.citydb.operation.exporter.hierarchy;

import org.citydb.database.schema.ObjectClass;
import org.citydb.model.address.Address;
import org.citydb.model.appearance.Appearance;
import org.citydb.model.common.Reference;
import org.citydb.model.common.ReferenceType;
import org.citydb.model.feature.Feature;
import org.citydb.model.feature.FeatureDescriptor;
import org.citydb.model.geometry.Geometry;
import org.citydb.model.geometry.ImplicitGeometry;
import org.citydb.model.property.*;
import org.citydb.operation.exporter.ExportException;
import org.citydb.operation.exporter.ExportHelper;
import org.citydb.operation.exporter.feature.FeatureExporter;
import org.citydb.operation.exporter.property.PropertyStub;

import java.sql.SQLException;
import java.util.stream.Collectors;

public class PropertyBuilder {
    private final ExportHelper helper;

    PropertyBuilder(ExportHelper helper) {
        this.helper = helper;
    }

    Property<?> build(PropertyStub propertyStub, Hierarchy hierarchy) throws ExportException, SQLException {
        if (propertyStub != null && propertyStub.getDataType() != null) {
            Property<?> property;
            switch (propertyStub.getDataType()) {
                case FEATURE_PROPERTY:
                    property = buildFeatureProperty(propertyStub, hierarchy);
                    break;
                case GEOMETRY_PROPERTY:
                    property = buildGeometryProperty(propertyStub, hierarchy);
                    break;
                case IMPLICIT_GEOMETRY_PROPERTY:
                    property = buildImplicitGeometryProperty(propertyStub, hierarchy);
                    break;
                case APPEARANCE_PROPERTY:
                    property = buildAppearanceProperty(propertyStub, hierarchy);
                    break;
                case ADDRESS_PROPERTY:
                    property = buildAddressProperty(propertyStub, hierarchy);
                    break;
                default:
                    property = buildAttribute(propertyStub);
            }

            if (property != null) {
                return property.setDescriptor(propertyStub.getDescriptor());
            }
        }

        return null;
    }

    <T extends Property<?>> T build(PropertyStub propertyStub, Hierarchy hierarchy, Class<T> type) throws ExportException, SQLException {
        Property<?> property = build(propertyStub, hierarchy);
        return type.isInstance(property) ? type.cast(property) : null;
    }

    private FeatureProperty buildFeatureProperty(PropertyStub propertyStub, Hierarchy hierarchy) throws ExportException, SQLException {
        Feature feature = hierarchy.getFeature(propertyStub.getFeatureId());
        if (feature != null) {
            ReferenceType referenceType = propertyStub.getReferenceType();
            if (referenceType != null) {
                if (hierarchy.isInlineFeature(propertyStub.getFeatureId())
                        || isTopLevel(feature)) {
                    return FeatureProperty.of(propertyStub.getName(),
                            Reference.of(helper.getOrCreateId(feature), referenceType));
                } else {
                    feature = helper.getTableHelper().getOrCreateExporter(FeatureExporter.class)
                            .doExport(propertyStub.getFeatureId(), hierarchy.getInlineFeatures());
                    hierarchy.addFeature(propertyStub.getFeatureId(), feature);
                }
            }

            return helper.lookupAndPut(feature) ?
                    FeatureProperty.of(propertyStub.getName(), Reference.of(
                            helper.getOrCreateId(feature),
                            ReferenceType.LOCAL_REFERENCE)) :
                    FeatureProperty.of(propertyStub.getName(), feature);
        }

        return null;
    }

    private GeometryProperty buildGeometryProperty(PropertyStub propertyStub, Hierarchy hierarchy) {
        Geometry<?> geometry = hierarchy.getGeometry(propertyStub.getGeometryId());
        return geometry != null ?
                GeometryProperty.of(propertyStub.getName(), geometry)
                        .setLod(propertyStub.getLod()) :
                null;
    }

    private ImplicitGeometryProperty buildImplicitGeometryProperty(PropertyStub propertyStub, Hierarchy hierarchy) {
        ImplicitGeometry implicitGeometry = hierarchy.getImplicitGeometry(propertyStub.getImplicitGeometryId());
        if (implicitGeometry != null) {
            ImplicitGeometryProperty property = helper.lookupAndPut(implicitGeometry) ?
                    ImplicitGeometryProperty.of(propertyStub.getName(), Reference.of(
                            helper.getOrCreateId(implicitGeometry),
                            ReferenceType.LOCAL_REFERENCE)) :
                    ImplicitGeometryProperty.of(propertyStub.getName(), implicitGeometry);

            if (propertyStub.getArrayValue() != null) {
                property.setTransformationMatrix(propertyStub.getArrayValue().getValues().stream()
                        .filter(Value::isDouble)
                        .map(Value::doubleValue)
                        .collect(Collectors.toList()));
            }

            return property.setReferencePoint(propertyStub.getReferencePoint())
                    .setLod(propertyStub.getLod());
        }

        return null;
    }

    private AppearanceProperty buildAppearanceProperty(PropertyStub propertyStub, Hierarchy hierarchy) {
        Appearance appearance = hierarchy.getAppearance(propertyStub.getAppearanceId());
        return appearance != null ?
                AppearanceProperty.of(propertyStub.getName(), appearance) :
                null;
    }

    private AddressProperty buildAddressProperty(PropertyStub propertyStub, Hierarchy hierarchy) {
        Address address = hierarchy.getAddress(propertyStub.getAddressId());
        if (address != null) {
            return helper.lookupAndPut(address) ?
                    AddressProperty.of(propertyStub.getName(), Reference.of(
                            helper.getOrCreateId(address),
                            ReferenceType.LOCAL_REFERENCE)) :
                    AddressProperty.of(propertyStub.getName(), address);
        }

        return null;
    }

    private Attribute buildAttribute(PropertyStub propertyStub) {
        return Attribute.of(propertyStub.getName(), propertyStub.getDataType())
                .setIntValue(propertyStub.getIntValue())
                .setDoubleValue(propertyStub.getDoubleValue())
                .setStringValue(propertyStub.getStringValue())
                .setArrayValue(propertyStub.getArrayValue())
                .setTimeStamp(propertyStub.getTimeStamp())
                .setURI(propertyStub.getURI())
                .setCodeSpace(propertyStub.getCodeSpace())
                .setUom(propertyStub.getUom())
                .setGenericContent(propertyStub.getGenericContent())
                .setGenericContentMimeType(propertyStub.getGenericContentMimeType());
    }

    private boolean isTopLevel(Feature feature) {
        return feature.getDescriptor()
                .map(FeatureDescriptor::getObjectClassId)
                .map(helper.getObjectClassHelper()::getObjectClass)
                .orElse(ObjectClass.UNDEFINED)
                .isTopLevel();
    }
}
