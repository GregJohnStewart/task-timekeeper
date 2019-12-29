package com.gjs.taskTimekeeper.webServer.server.pojo;

import com.mongodb.client.MongoCollection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

public interface MongoObject {
	public ObjectId getId();
}
