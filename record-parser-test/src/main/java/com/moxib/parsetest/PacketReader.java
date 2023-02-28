package com.moxib.parsetest;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.parsetools.RecordParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketReader {
  private final static Logger logger = LoggerFactory.getLogger(PacketReader.class);

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();

    AsyncFile file = vertx.fileSystem().openBlocking("packet.db",
      new OpenOptions().setRead(true));
    // <klen><SP><vlen><SP><key><value>
    RecordParser parser = RecordParser.newDelimited(Buffer.buffer(" "), file);
    parser.pause();
    parser.fetch(1);
    parser.handler(buffer -> readKlen(buffer, parser));
    parser.endHandler(v -> vertx.close());
  }

  private static void readKlen(Buffer buffer, RecordParser parser) {
    int klen = buffer.getInt(0);
    logger.info("klen =  {}", klen);
    parser.handler(vlength -> readVlen(vlength, parser, klen));
    parser.fetch(1);

  }

  private static void readVlen(Buffer vlength, RecordParser parser, int klen) {
    int vlen = vlength.getInt(0);
    logger.info("vlen = {}" , vlen);
    parser.handler(keyBuffer -> readKeyAndValue(keyBuffer, parser, klen, vlen));
    parser.fetch(1);
  }

  private static void readKeyAndValue(Buffer keyBuffer, RecordParser parser, int klen, int vlen) {
    parser.fixedSizeMode(klen);
    String key = keyBuffer.getString(0, klen);
    String value = keyBuffer.getString(klen, klen +vlen);
    logger.info("key = {}" , key);
    logger.info("value = {}" , value);
  }

}
