package com.gjs.taskTimekeeper.webServer.server.codec;

import com.gjs.taskTimekeeper.webServer.server.pojo.User;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class UserCodec extends OurCodec<User> {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserCodec.class);

	public UserCodec() {
		super(User.class);
	}

	@Override
	public User decode(BsonReader reader, DecoderContext decoderContext) {
		//TODO:: find an automated way to do this
		Document document = this.documentCodec.decode(reader, decoderContext);
		LOGGER.debug("Decoding the following document: {}", document);
		User fruit = new User(
			document.getString("username"),
			document.getString("hashedPass"),
			document.getString("email"),
			null,
			null,
			null,
			new ArrayList<>()
		);
		fruit.set_id(document.getObjectId("_id"));
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
}
