package com.gjs.taskTimekeeper.webServer.server.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class MongoObject {
	@JsonIgnore
	protected ObjectId _id;
}
