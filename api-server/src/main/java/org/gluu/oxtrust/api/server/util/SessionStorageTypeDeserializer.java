package org.gluu.oxtrust.api.server.util;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import java.io.IOException;

public class SessionStorageTypeDeserializer extends JsonDeserializer<SessionStorageType> {

	@Override
	public SessionStorageType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
			throws IOException, JsonProcessingException {
		JsonNode node = jsonParser.getCodec().readTree(jsonParser);
		return SessionStorageType.from(node.asText());
	}

}