package me.ikevoodoo.datamanager;

import me.ikevoodoo.datamanager.api.CustomType;
import me.ikevoodoo.datamanager.api.TypeRegistry;
import me.ikevoodoo.datamanager.load.CorruptDataException;
import me.ikevoodoo.datamanager.load.DataLoader;
import me.ikevoodoo.datamanager.save.DataSaver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Ref;

/**
 * Class for easy access to DataLoader and DataSaver, however you can make your own instances.
 *
 * @see DataLoader
 * @see DataSaver
 */
@SuppressWarnings("unused")
public class DataManager {

    private static final DataLoader DATA_LOADER = new DataLoader();
    private static final DataSaver DATA_SAVER = new DataSaver();

    public static void saveData(Data data, File file) {
        DATA_SAVER.saveData(data, file);
    }

    public static void saveData(Data data, Path path) {
        DATA_SAVER.saveData(data, path.toFile());
    }

    public static void saveData(Data data, String path) {
        DATA_SAVER.saveData(data, new File(path));
    }

    public static Data loadData(File file) throws IllegalArgumentException,
            CorruptDataException, IOException {
        return DATA_LOADER.loadData(file);
    }

    public static Data loadData(Path path) throws IllegalArgumentException,
            CorruptDataException, IOException {
        return DATA_LOADER.loadData(path.toFile());
    }

    public static Data loadData(String path) throws IllegalArgumentException,
            CorruptDataException, IOException {
        return DATA_LOADER.loadData(new File(path));
    }

    private static void printRef(Reference ref, int indentation) {
        System.out.println(" ".repeat(4).repeat(indentation - 1) + "Reference: (" + ref.getIndex() + ")");
        ref.getObjects().forEach(obj -> {
            if(obj instanceof Reference) printRef((Reference)obj, indentation + 1);
            else if(obj instanceof DataFragment) print((DataFragment) obj, indentation + 1);
            else System.out.println(" ".repeat(4).repeat(indentation - 1) + " -> " + obj.getClass().getName()+ ": " + obj + " (" + ref.getIndex() + ")");
        });
    }

    private static void print(DataFragment frag, int indentation) {
        System.out.println(" ".repeat(4).repeat(indentation - 1) + frag.name + ":");
        frag.getObjects().forEach(obj -> {
            if(obj instanceof DataFragment) print((DataFragment) obj, indentation + 1);
            else if(obj instanceof Reference) {
                printRef((Reference) obj, indentation + 1);
            }
            else System.out.println(" ".repeat(4).repeat(indentation) + "- " + obj.getClass().getName()+ ": " + obj + " (" + indentation + ")");
        });
    }

    public static void main(String[] args) throws Exception {
        //DATA_LOADER.setOption("-ReferenceCopy", true);
        Data data = DATA_LOADER.loadData(new File("C:\\Users\\admin\\Desktop\\testload"));
        data.getFragments().forEach(frag -> print(frag, 1));
    }

}
