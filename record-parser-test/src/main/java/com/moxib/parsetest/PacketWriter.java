package com.moxib.parsetest;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;

public class PacketWriter {
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    AsyncFile file = vertx.fileSystem().openBlocking("packet.db",
      new OpenOptions().setWrite(true).setCreate(true));
    String key = "sampleKey";
    String value = "sampleValue";
    Buffer buffer = Buffer.buffer();
    //<klen><SP><vlen><SP><key><value>
    // klen
    buffer.appendInt(key.length());
    // " "
    buffer.appendString(" ");
    // vlen
    buffer.appendInt(value.length());
    // " "
    buffer.appendString(" ");
    // key
    buffer.appendString(key);
    // value
    buffer.appendString(value);
    file.end(buffer, ar -> vertx.close());
  }
}
