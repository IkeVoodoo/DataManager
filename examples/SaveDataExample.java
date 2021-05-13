package me.ikevoodoo.datamanager.examples;

import me.ikevoodoo.datamanager.Data;
import me.ikevoodoo.datamanager.DataManager;

/**
 * An example depicting how to save data to a file.
 */
public class SaveDataExample {

    public static void main(String[] args) {
        Data data = // Get some data.

        // Save the data.
        DataManager.saveData(data, "C:\\My Files\\SaveExampleFile.txt");

    }

}
