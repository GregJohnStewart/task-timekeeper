package com.gjs.taskTimekeeper.webServer.server.codec;

import com.gjs.taskTimekeeper.webServer.server.pojo.User;
import com.mongodb.MongoClient;
import org.bson.BsonObjectId;
import org.bson.BsonReader;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.ObjectIdGenerator;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class UserCodec implements CollectibleCodec<User> {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserCodec.class);
	private final Codec<Document> documentCodec;
	private final ObjectIdGenerator idGenerator = new ObjectIdGenerator();

	public UserCodec() {
		this.documentCodec = MongoClient.getDefaultCodecRegistry()
			                     .get(Document.class);
	}


	@Override
	public User generateIdIfAbsentFromDocument(User document) {
		if (!documentHasId(document)) {
			document.set_id((ObjectId) idGenerator.generate());
		}
		return document;
	}

	@Override
	public boolean documentHasId(User document) {
		return document.get_id() != null;
	}

	@Override
	public BsonValue getDocumentId(User document) {
		return new BsonObjectId(document.get_id());
	}

	@Override
	public User decode(BsonReader reader, DecoderContext decoderContext) {
		//TODO:: find an automated way to do this
		Document document = this.documentCodec.decode(reader, decoderContext);
		LOGGER.debug("Decoding the following document: {}", document);
		User fruit = new User(
			document.getObjectId("_id"),
			document.getString("username"),
			document.getString("hashedPass"),
			document.getString("email"),
			null,
			null,
			null,
			new ArrayList<>()
		);
		return fruit;
	}

	@Override
	public void encode(BsonWriter writer, User value, EncoderContext encoderContext) {
		//TODO:: find an automated way to do this
		Document doc = new Document();
		doc.put("_id", value.get_id());
		doc.put("username", value.getUsername());
		doc.put("hashedPass", value.getHashedPass());
		doc.put("email", value.getEmail());
		documentCodec.encode(writer, doc, encoderContext);
	}

	@Override
	public Class<User> getEncoderClass() {
		return User.class;
	}
}
