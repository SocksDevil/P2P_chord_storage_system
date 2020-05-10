package com.feup.sdis.peer;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SerializationUtils {
    private static final int messageSize = Constants.BLOCK_SIZE * 2;

    public static <T> T deserialize(AsynchronousSocketChannel socket) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(messageSize);
            Future<Integer> readResult = socket.read(buffer);
            readResult.get();
            buffer.flip();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);

            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            ObjectInputStream in = new ObjectInputStream(inputStream);
            return (T) in.readObject();
        } catch (IOException | ClassNotFoundException | InterruptedException | ExecutionException e) {
            System.out.println("Failed to deserialize object!");
            e.printStackTrace();
            return null;
        }
    }

    public static <T> ByteBuffer serialize(T obj) {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream(messageSize);
        try (ObjectOutputStream out = new ObjectOutputStream(stream)) {
            out.writeObject(obj);
            out.flush();
        } catch (IOException e) {
            System.out.println("Failed to serialize object!");
            return null;
        }

        return ByteBuffer.wrap(stream.toByteArray());
    }
}
