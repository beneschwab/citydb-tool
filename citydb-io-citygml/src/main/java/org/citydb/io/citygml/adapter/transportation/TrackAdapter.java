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

package org.citydb.io.citygml.adapter.transportation;

import org.citydb.io.citygml.annotation.DatabaseType;
import org.citydb.io.citygml.builder.ModelBuildException;
import org.citydb.io.citygml.reader.ModelBuilderHelper;
import org.citydb.io.citygml.serializer.ModelSerializeException;
import org.citydb.io.citygml.writer.ModelSerializerHelper;
import org.citydb.model.common.Name;
import org.citydb.model.common.Namespaces;
import org.citydb.model.feature.Feature;
import org.citydb.model.feature.FeatureType;
import org.citydb.model.property.FeatureProperty;
import org.citygml4j.core.model.transportation.IntersectionProperty;
import org.citygml4j.core.model.transportation.SectionProperty;
import org.citygml4j.core.model.transportation.Track;

@DatabaseType(name = "Track", namespace = Namespaces.TRANSPORTATION)
public class TrackAdapter extends AbstractTransportationSpaceAdapter<Track> {

    @Override
    public Feature createModel(Track source) throws ModelBuildException {
        return Feature.of(FeatureType.TRACK);
    }

    @Override
    public void build(Track source, Feature target, ModelBuilderHelper helper) throws ModelBuildException {
        super.build(source, target, helper);
        helper.addStandardObjectClassifiers(source, target, Namespaces.TRANSPORTATION);

        if (source.isSetSections()) {
            for (SectionProperty property : source.getSections()) {
                helper.addFeature(Name.of("section", Namespaces.TRANSPORTATION), property, target);
            }
        }

        if (source.isSetIntersections()) {
            for (IntersectionProperty property : source.getIntersections()) {
                helper.addFeature(Name.of("intersection", Namespaces.TRANSPORTATION), property, target);
            }
        }
    }

    @Override
    public Track createObject(Feature source) throws ModelSerializeException {
        return new Track();
    }

    @Override
    public void serialize(Feature source, Track target, ModelSerializerHelper helper) throws ModelSerializeException {
        super.serialize(source, target, helper);
        helper.addStandardObjectClassifiers(source, target, Namespaces.TRANSPORTATION);

        for (FeatureProperty property : source.getFeatures().get(Name.of("section", Namespaces.TRANSPORTATION))) {
            target.getSections().add(helper.getObjectProperty(property, SectionPropertyAdapter.class));
        }

        for (FeatureProperty property : source.getFeatures().get(Name.of("intersection", Namespaces.TRANSPORTATION))) {
            target.getIntersections().add(helper.getObjectProperty(property, IntersectionPropertyAdapter.class));
        }
    }
}
