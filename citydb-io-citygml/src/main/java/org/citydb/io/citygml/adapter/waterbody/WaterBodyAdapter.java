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

package org.citydb.io.citygml.adapter.waterbody;

import org.citydb.io.citygml.adapter.core.AbstractOccupiedSpaceAdapter;
import org.citydb.io.citygml.adapter.core.SpaceGeometrySupport;
import org.citydb.io.citygml.adapter.geometry.builder.Lod;
import org.citydb.io.citygml.adapter.geometry.serializer.MultiCurvePropertyAdapter;
import org.citydb.io.citygml.adapter.geometry.serializer.MultiSurfacePropertyAdapter;
import org.citydb.io.citygml.adapter.geometry.serializer.SolidPropertyAdapter;
import org.citydb.io.citygml.annotation.DatabaseType;
import org.citydb.io.citygml.builder.ModelBuildException;
import org.citydb.io.citygml.reader.ModelBuilderHelper;
import org.citydb.io.citygml.serializer.ModelSerializeException;
import org.citydb.io.citygml.writer.ModelSerializerHelper;
import org.citydb.model.common.Name;
import org.citydb.model.common.Namespaces;
import org.citydb.model.feature.Feature;
import org.citydb.model.feature.FeatureType;
import org.citydb.model.property.GeometryProperty;
import org.citygml4j.core.model.CityGMLVersion;
import org.citygml4j.core.model.deprecated.waterbody.DeprecatedPropertiesOfWaterBody;
import org.citygml4j.core.model.waterbody.WaterBody;

@DatabaseType(name = "WaterBody", namespace = Namespaces.WATER_BODY)
public class WaterBodyAdapter extends AbstractOccupiedSpaceAdapter<WaterBody> {

    @Override
    public Feature createModel(WaterBody source) throws ModelBuildException {
        return Feature.of(FeatureType.WATER_BODY);
    }

    @Override
    public void build(WaterBody source, Feature target, ModelBuilderHelper helper) throws ModelBuildException {
        super.build(source, target, helper);
        helper.addStandardObjectClassifiers(source, target, Namespaces.WATER_BODY);

        if (source.hasDeprecatedProperties()) {
            DeprecatedPropertiesOfWaterBody properties = source.getDeprecatedProperties();
            if (properties.getLod1MultiCurve() != null) {
                helper.addCurveGeometry(Name.of("lod1MultiCurve", Namespaces.DEPRECATED),
                        properties.getLod1MultiCurve(), Lod.of(1), target);
            }

            if (properties.getLod1MultiSurface() != null) {
                helper.addSurfaceGeometry(Name.of("lod1MultiSurface", Namespaces.DEPRECATED),
                        properties.getLod1MultiSurface(), Lod.of(1), target);
            }

            if (properties.getLod4Solid() != null) {
                helper.addSolidGeometry(Name.of("lod4Solid", Namespaces.DEPRECATED),
                        properties.getLod4Solid(), Lod.of(4), target);
            }
        }
    }

    @Override
    public WaterBody createObject(Feature source) throws ModelSerializeException {
        return new WaterBody();
    }

    @Override
    public void serialize(Feature source, WaterBody target, ModelSerializerHelper helper) throws ModelSerializeException {
        super.serialize(source, target, helper);
        helper.addStandardObjectClassifiers(source, target, Namespaces.WATER_BODY);
    }

    @Override
    public void postSerialize(Feature source, WaterBody target, ModelSerializerHelper helper) throws ModelSerializeException {
        super.postSerialize(source, target, helper);

        if (helper.getCityGMLVersion() == CityGMLVersion.v2_0
                && source.getGeometries().containsNamespace(Namespaces.DEPRECATED)) {
            GeometryProperty lod1MultiCurve = source.getGeometries()
                    .getFirst(Name.of("lod1MultiCurve", Namespaces.DEPRECATED))
                    .orElse(null);
            if (lod1MultiCurve != null) {
                target.getDeprecatedProperties().setLod1MultiCurve(
                        helper.getGeometryProperty(lod1MultiCurve, MultiCurvePropertyAdapter.class));
            }

            GeometryProperty lod1MultiSurface = source.getGeometries()
                    .getFirst(Name.of("lod1MultiSurface", Namespaces.DEPRECATED))
                    .orElse(null);
            if (lod1MultiSurface != null) {
                target.getDeprecatedProperties().setLod1MultiSurface(
                        helper.getGeometryProperty(lod1MultiSurface, MultiSurfacePropertyAdapter.class));
            }

            GeometryProperty lod4Solid = source.getGeometries()
                    .getFirst(Name.of("lod4Solid", Namespaces.DEPRECATED))
                    .orElse(null);
            if (lod4Solid != null) {
                target.getDeprecatedProperties().setLod4Solid(
                        helper.getGeometryProperty(lod4Solid, SolidPropertyAdapter.class));
            }
        }
    }

    @Override
    protected void configureSerializer(SpaceGeometrySupport<WaterBody> geometrySupport) {
        geometrySupport.withLod0MultiSurface()
                .withLod0MultiCurve()
                .withLod1Solid()
                .withLod2Solid()
                .withLod3Solid();
    }
}
