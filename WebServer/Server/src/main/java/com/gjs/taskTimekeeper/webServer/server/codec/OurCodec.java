package com.gjs.taskTimekeeper.webServer.server.codec;

import com.gjs.taskTimekeeper.webServer.server.pojo.MongoObject;
import com.mongodb.MongoClient;
import org.bson.BsonObjectId;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.ObjectIdGenerator;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class OurCodec <T extends MongoObject> implements CollectibleCodec<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OurCodec.class);

    private final Class<T> tClass;
    protected final Codec<Document> documentCodec;
    private final ObjectIdGenerator idGenerator = new ObjectIdGenerator();

    public OurCodec(Class<T> tClass){
        this.tClass = tClass;
        this.documentCodec = MongoClient.getDefaultCodecRegistry()
                .get(Document.class);
    }

    @Override
    public Class<T> getEncoderClass() {
        return this.tClass;
    }


    @Override
    public T generateIdIfAbsentFromDocument(T document) {
        if (!documentHasId(document)) {
            document.set_id((ObjectId) idGenerator.generate());
        }
        return document;
    }

    @Override
    public boolean documentHasId(T document) {
        return document.get_id() != null;
    }

    @Override
    public BsonValue getDocumentId(T document) {
        return new BsonObjectId(document.get_id());
    }
}
