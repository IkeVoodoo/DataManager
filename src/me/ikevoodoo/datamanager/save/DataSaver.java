package me.ikevoodoo.datamanager.save;

import me.ikevoodoo.datamanager.Data;
import me.ikevoodoo.datamanager.DataFragment;

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
        try(PrintWriter clearer = new PrintWriter(new FileWriter(file, false))) {
            clearer.print("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        long start = System.nanoTime();
        try(FileChannel channel = new RandomAccessFile(file, "rw").getChannel()) {
            for(DataFragment fragment : data.getFragments()) {
                builder.append(">FRAG ").append(fragment.id).append(" ").append(fragment.name).append('\n');
                for(Object obj : fragment.getObjects()) {
                    if(obj instanceof String)
                        builder.append("str: ").append(obj).append('\n');
                    else if (obj instanceof Integer)
                        builder.append("int: ").append(obj).append('\n');
                    else if (obj instanceof Boolean)
                        builder.append("bool: ").append(obj).append('\n');
                }
                builder.append(">END\n\n");
            }
            channel.map(FileChannel.MapMode.READ_WRITE, 0, builder.length()).put(builder.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(System.nanoTime() - start);
    }

}
