package me.ikevoodoo.data.examples;

import me.ikevoodoo.data.Data;
import me.ikevoodoo.data.DataManager;

/**
 * An example depicting how to save data to a file.
 */
public class SaveDataExample {

    public static void main(String[] args) {
        Data data = new Data(); // Get some data.

        // Save the data.
        DataManager.saveData(data, "C:\\My Files\\SaveExampleFile.txt");

    }

}
