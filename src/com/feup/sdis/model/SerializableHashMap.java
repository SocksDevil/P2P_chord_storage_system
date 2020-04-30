package com.feup.sdis.model;

import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SerializableHashMap<T> {

    private String filename;
    protected Map<String, T> files = new ConcurrentHashMap<>();

    SerializableHashMap(String filename) {
        this.filename = filename;
        final File hashFile = new File(filename);
        try {
            if (!hashFile.exists())
                hashFile.createNewFile();
            else {
                final FileInputStream file = new FileInputStream(filename);
                final ObjectInputStream inputStream = new ObjectInputStream(file);
                this.files = (ConcurrentHashMap<String, T>) inputStream.readObject();
                inputStream.close();
                file.close();
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            updateObject();
        }
    }

    protected synchronized void updateObject(){
        try {
            final FileOutputStream outputStream = new FileOutputStream(filename);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(files);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized T getOrDefault(String s, T set) {
        return files.getOrDefault(s, set);
    }

    public synchronized T get(String s) {
        return files.get(s);
    }

    public synchronized int size() {
        return files.size();
    }

    public synchronized T put(String s, T v) {
        T value = files.put(s, v);
        this.updateObject();
        return value;
    }

    public synchronized T remove(String s) {
        final T returnValue = files.remove(s);
        this.updateObject();
        return returnValue;
    }

    public synchronized Set<Map.Entry<String, T>> entrySet(){
        return this.files.entrySet();
    }

    public synchronized boolean containsKey(String key){
        return files.containsKey(key);
    }
}
