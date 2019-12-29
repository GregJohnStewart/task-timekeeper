package com.gjs.taskTimekeeper.webServer.server.codec;

import com.gjs.taskTimekeeper.webServer.server.pojo.User;
import com.mongodb.MongoClient;
import org.bson.BsonObjectId;
import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

public class UserCodec implements CollectibleCodec<User> {
	private final Codec<Document> documentCodec;

	public UserCodec() {
		this.documentCodec = MongoClient.getDefaultCodecRegistry()
			                     .get(Document.class);
	}


	@Override
	public User generateIdIfAbsentFromDocument(User document) {
		if (!documentHasId(document)) {
			document.setId(ObjectId.get());
		}
		return document;
	}

	@Override
	public boolean documentHasId(User document) {
		return document.getId() != null;
	}

	@Override
	public BsonValue getDocumentId(User document) {
		return new BsonObjectId(document.getId());
	}

	@Override
	public User decode(BsonReader reader, DecoderContext decoderContext) {
		//TODO:: find an automated way to do this
		Document document = this.documentCodec.decode(reader, decoderContext);
		User fruit = new User(
			document.getObjectId("id"),
			document.getString("username"),
			document.getString("hashedPass"),
			document.getList("roles", String.class)
		);
		return fruit;
	}

	@Override
	public void encode(BsonWriter writer, User value, EncoderContext encoderContext) {
		//TODO:: find an automated way to do this
		Document doc = new Document();
		doc.put("id", value.getId());
		doc.put("username", value.getUsername());
		doc.put("hashedPass", value.getHashedPass());
		doc.put("roles", value.getRoles());
		documentCodec.encode(writer, doc, encoderContext);
	}

	@Override
	public Class<User> getEncoderClass() {
		return User.class;
	}
}
