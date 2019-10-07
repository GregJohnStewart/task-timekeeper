package com.gjs.taskTimekeeper.backend.serialization;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.gjs.taskTimekeeper.backend.Task;

import java.io.IOException;

public class TaskNameSerializer extends JsonSerializer<Task> {
	@Override
	public void serialize(Task value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeObject(value.getName());
	}
}