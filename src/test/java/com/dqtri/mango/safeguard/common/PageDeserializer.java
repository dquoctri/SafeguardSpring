package com.dqtri.mango.safeguard.common;

import com.dqtri.mango.safeguard.model.SafeguardUser;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class PageDeserializer<T> extends JsonDeserializer<Page<T>> {
    @Override
    public Page<T> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        // Extract the necessary fields from the JSON node
        JsonNode contentNode = node.get("content");
        List<T> content = new ArrayList<>();
        // Deserialize the content list
        if (contentNode.isArray()) {
            for (JsonNode elementNode : contentNode) {
                // Deserialize each element in the content list
                T element = deserializationContext.readValue(elementNode.traverse(), typeReference(deserializationContext));
                content.add(element);
            }
        }
        // Extract other fields as needed (e.g., totalElements, pageable, etc.)
        // Build and return the Page object
        return new PageImpl<>(content);
    }

    protected JavaType typeReference(DeserializationContext deserializationContext) {
        // Construct the JavaType reference for deserializing the content elements
        TypeFactory typeFactory = deserializationContext.getTypeFactory();
        // For example, if the content type is SafeguardUser:
        return typeFactory.constructType(SafeguardUser.class);
    }
}