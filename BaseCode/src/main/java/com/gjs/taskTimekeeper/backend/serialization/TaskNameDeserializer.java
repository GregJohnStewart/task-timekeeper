package com.gjs.taskTimekeeper.backend.serialization;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.gjs.taskTimekeeper.backend.Task;

import java.io.IOException;

public class TaskNameDeserializer extends JsonDeserializer<Task> {

	@Override
	public Task deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		JsonNode node = p.getCodec().readTree(p);
		//TODO:: make this work
		String taskName = (String) (node.get("name")).asText();
		int userId = (Integer) ((IntNode) node.get("createdBy")).numberValue();

		return null;
	}
}