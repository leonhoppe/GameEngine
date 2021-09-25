package de.craftix.engine;

import java.io.*;

public final class ObjectIO {
    private ObjectIO() {}

    public static void write(File file, Serializable object) {
        try {

            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(object);
            objectOut.close();

        } catch (Exception ex) {
            GameEngine.throwError(ex);
        }
    }

    public static <T extends Serializable> T read(File file) {
        try {

            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            T obj = (T) objectIn.readObject();

            objectIn.close();
            return obj;

        } catch (Exception ex) {
            GameEngine.throwError(ex);
            return null;
        }
    }

}
