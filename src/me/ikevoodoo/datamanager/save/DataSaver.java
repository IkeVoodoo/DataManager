package me.ikevoodoo.datamanager.save;

import me.ikevoodoo.datamanager.Data;
import me.ikevoodoo.datamanager.DataFragment;
import me.ikevoodoo.datamanager.api.TypeRegistry;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


public class DataSaver {

    /**
     * Saves a data object to a file, the file's contents will be cleared.
     * @param data The data object to save.
     * @param file The file to write into.
     * @see Data
     * @see File
     */
    public void saveData(Data data, File file) {
        if(file.mkdirs())
            System.out.println("Created the file's parent directories since they did not exist!");
        if(file.isDirectory() && !file.delete())
            throw new IllegalStateException("Unable to continue with saving data.");
        try(PrintWriter clearer = new PrintWriter(new FileWriter(file, false))) {
            clearer.print("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        try(FileChannel channel = new RandomAccessFile(file, "rw").getChannel()) {
            for(DataFragment fragment : data.getFragments()) {
                builder.append(">FRAG ").append(fragment.name).append('\n');
                for(Object obj : fragment.getObjects()) {
                    String name = TypeRegistry.getTypeName(obj.getClass());
                    if(name == null)
                        throw new IllegalStateException("Attempting to save unregistered data type: " + obj.getClass());
                    builder.append(name).append(": ").append(obj).append("\n");
                }
                builder.append(">END\n\n");
            }
            channel.map(FileChannel.MapMode.READ_WRITE, 0, builder.length()).put(builder.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
