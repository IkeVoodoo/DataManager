package me.ikevoodoo.datamanager.examples;

import me.ikevoodoo.datamanager.Data;
import me.ikevoodoo.datamanager.DataManager;

/**
 * An example depicting how to edit data from a file.
 */
public class EditDataExample {

    public static void main(String[] args) {
        // Load the file to a Data object.
        Data data = DataManager.loadData("C:\\My Files\\EditExampleFile.txt");

        // Edit the data object.
        data.getFragments().get(0).name = "Some Name!";

        // Save the data object to the file.
        DataManager.saveData(data, "C:\\My Files\\EditExampleFile.txt");
    }

}
