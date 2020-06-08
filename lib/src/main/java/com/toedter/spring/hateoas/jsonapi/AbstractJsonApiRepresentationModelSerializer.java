/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.toedter.spring.hateoas.jsonapi;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;

import java.io.IOException;

public abstract class AbstractJsonApiRepresentationModelSerializer<T extends RepresentationModel<?>>
        extends AbstractJsonApiSerializer<T> {

    protected AbstractJsonApiRepresentationModelSerializer(Class<T> t) {
        super(t);
    }

    protected AbstractJsonApiRepresentationModelSerializer(Class<?> t, boolean dummy) {
        super(t, dummy);
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        JsonApiDocument doc;
        final JsonStreamContext outputContext = gen.getOutputContext();
        if (outputContext.inRoot()) {
            doc = new JsonApiDocument()
                    .withJsonapi(new JsonApiJsonApi())
                    .withData(JsonApiData.extractCollectionContent(value))
                    .withLinks(getLinksOrNull(value));
            doc = postProcess(value, doc);
        } else {
            doc = new JsonApiDocument()
                    .withData(JsonApiData.extractCollectionContent(value));
        }

        provider
                .findValueSerializer(JsonApiDocument.class)
                .serialize(doc, gen, provider);
    }

    protected JsonApiDocument postProcess(T value, JsonApiDocument doc) {
        return doc;
    }

    Links getLinksOrNull(RepresentationModel<?> representationModel) {
        Links links = representationModel.getLinks();
        if (links.isEmpty()) {
            links = null;
        }
        return links;
    }
}