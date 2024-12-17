package common.utils;

import java.io.*;
import java.sql.Timestamp;

public class TimestampSerializer {

    // Timestamp Serializer
    public static void serialize(DataOutputStream dataStream, Timestamp timestamp) throws IOException {
        dataStream.writeLong(timestamp.getTime());  // Serializa como milisegundos
    }

    public static Timestamp deserialize(DataInputStream dataStream) throws IOException {
        long timestamp = dataStream.readLong();
        return new Timestamp(timestamp);
    }
}

