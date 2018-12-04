package net.cassite.f;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class MListDeserializer<E> extends StdDeserializer<MList<E>> implements ContextualDeserializer {
    private JsonDeserializer<E> contentDeser;

    @SuppressWarnings("unused")
    public MListDeserializer() {
        super(MList.class);
    }

    private MListDeserializer(JavaType collectionType, JsonDeserializer<E> contentDeser) {
        super(collectionType);
        this.contentDeser = contentDeser;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        // property == null || property.getType().isCollectionLikeType();
        JavaType contentType = ctxt.getContextualType().getContentType();
        return new MListDeserializer<>(ctxt.getContextualType(), ctxt.findContextualValueDeserializer(contentType, property));
    }

    @Override
    public MList<E> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // contentDeser != null;
        // p.currentToken() == JsonToken.START_ARRAY;
        MList<E> ls = MList.modifiable();
        for (JsonToken t = p.nextToken(); t != JsonToken.END_ARRAY; t = p.nextToken()) {
            E value = contentDeser.deserialize(p, ctxt);
            ls.add(value);
        }
        return ls.immutable();
    }
}
