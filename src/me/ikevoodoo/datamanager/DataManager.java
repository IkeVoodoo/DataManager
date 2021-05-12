package me.ikevoodoo.datamanager;

import me.ikevoodoo.datamanager.load.DataLoader;
import me.ikevoodoo.datamanager.save.DataSaver;

import java.io.File;
import java.nio.file.Path;

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

    public static Data loadData(File file) {
        return DATA_LOADER.loadData(file);
    }

    public static Data loadData(Path path) {
        return DATA_LOADER.loadData(path.toFile());
    }

    public static Data loadData(String path) {
        return DATA_LOADER.loadData(new File(path));
    }

}
