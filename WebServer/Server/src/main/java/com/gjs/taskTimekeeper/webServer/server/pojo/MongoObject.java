package com.gjs.taskTimekeeper.webServer.server.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class MongoObject {
	private ObjectId _id;
}
