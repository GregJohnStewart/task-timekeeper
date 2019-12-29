package com.gjs.taskTimekeeper.webServer.server.service.mongo;

import com.gjs.taskTimekeeper.webServer.server.pojo.MongoObject;
import com.gjs.taskTimekeeper.webServer.server.pojo.User;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.types.ObjectId;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.all;
import static com.mongodb.client.model.Filters.eq;

public abstract class MongoService<T extends MongoObject> {
	protected final String database;
	protected final String collection;
	final Class<T> typeParameterClass;

	//TODO:: get database name from configuration
	public MongoService(String database, String collection, Class<T> typeParameterClass) {
		this.database = database;
		this.collection = collection;
		this.typeParameterClass = typeParameterClass;
	}

	@Inject
	protected MongoClient mongoClient;

	public void add(T object){
		getCollection().insertOne(object);
	}

	public List<T> list(){
		List<T> list = new ArrayList<>();
		MongoCursor<T> cursor = getCollection().find().iterator();

		try {
			while (cursor.hasNext()) {
				list.add(cursor.next());
			}
		} finally {
			cursor.close();
		}
		return list;
	}

	public T get(ObjectId id){
		MongoCursor<T> cursor = getCollection().find(
			eq("id", id)
		).iterator();


		try {
			//TODO:: make better exceptions
			if(!cursor.hasNext()){
				throw new RuntimeException("No object found");
			}
			T result = cursor.next();
			if(cursor.hasNext()){
				throw new RuntimeException("Multiple objects found....");
			}
			return result;
		} finally {
			cursor.close();
		}
	}

	public void remove(String id){
		getCollection().deleteOne(eq("id", new ObjectId(id)));
	}

	public void removeAll(){
		BasicDBObject document = new BasicDBObject();
		// Delete All documents from collection Using blank BasicDBObject
		getCollection().deleteMany(document);
	}

	public void remove(ObjectId id){
		this.remove(id.toString());
	}

	public void remove(T object){
		this.remove(object.getId());
	}

	private MongoCollection<T> getCollection(){
		return mongoClient.getDatabase(this.database).getCollection(this.collection, typeParameterClass);
	}
}
