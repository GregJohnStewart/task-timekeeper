package com.gjs.taskTimekeeper.webServer.server.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gjs.taskTimekeeper.webServer.server.pojo.MongoObject;
import org.bson.BsonObjectId;
import org.bson.BsonReader;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.ObjectIdGenerator;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OurCodec <T extends MongoObject> implements CollectibleCodec<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OurCodec.class);

    private final Class<T> tClass;
    protected final Codec<Document> documentCodec;
    private final ObjectIdGenerator idGenerator = new ObjectIdGenerator();
    protected final ObjectMapper mapper;

    public OurCodec(Class<T> tClass, ObjectMapper mapper){
        this.tClass = tClass;
        this.mapper = mapper;
        this.documentCodec = new DocumentCodec();
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


    @Override
    public T decode(BsonReader reader, DecoderContext decoderContext) {
        Document document = this.documentCodec.decode(reader, decoderContext);
        LOGGER.debug("Decoding the following document: {}", document);

        T value = this.mapper.convertValue(document, this.tClass);
        value.set_id(document.getObjectId("_id"));

        return value;
    }

    @Override
    public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
        LOGGER.debug("Encoding the following value: {}", value);
        Document doc = this.mapper.convertValue(value, Document.class);
        doc.append("_id", value.get_id());
        LOGGER.debug("encoded value: {}", doc);
        documentCodec.encode(writer, doc, encoderContext);
    }
}
