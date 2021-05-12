package me.ikevoodoo.datamanager.save;

import me.ikevoodoo.datamanager.Data;
import me.ikevoodoo.datamanager.DataFragment;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;


public class DataSaver {

    /**
     * Saves a data object to a file, the file's contents will be cleared.
     * @param data The data object to save.
     * @param file The file to write into.
     * @see Data
     * @see File
     */
    public void saveData(Data data, File file) {
        try(PrintWriter clearer = new PrintWriter(new FileWriter(file, false))) {
            clearer.print("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try(PrintWriter printWriter = new PrintWriter(file)) {
            for(DataFragment fragment : data.getFragments()) {
                printWriter.println(">FRAG " + fragment.id + " " + fragment.name);
                for(Object obj : fragment.getObjects()) {
                    if(obj instanceof String)
                        printWriter.println("str: " + obj);
                    else if (obj instanceof Integer)
                        printWriter.println("int: " + obj);
                    else if (obj instanceof Boolean)
                        printWriter.println("bool: " + obj);
                }
                printWriter.println(">END\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
