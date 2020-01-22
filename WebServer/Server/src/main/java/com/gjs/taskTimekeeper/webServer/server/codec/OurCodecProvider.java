package com.gjs.taskTimekeeper.webServer.server.codec;

import com.gjs.taskTimekeeper.webServer.server.pojo.Group;
import com.gjs.taskTimekeeper.webServer.server.pojo.User;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class OurCodecProvider implements CodecProvider {
	@Override
	public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
		if (clazz == User.class) {
			return (Codec<T>) new OurCodec<>(User.class, User.getObjectMapper());
		}
		if (clazz == Group.class) {
			return (Codec<T>) new OurCodec<>(Group.class, Group.getObjectMapper());
		}
		return null;
	}
}
