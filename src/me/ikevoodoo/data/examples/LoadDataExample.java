package me.ikevoodoo.data.examples;

import me.ikevoodoo.data.Data;
import me.ikevoodoo.data.DataFragment;
import me.ikevoodoo.data.DataManager;

/**
 * An example depicting how to load data from a file.
 */
public class LoadDataExample {

    public static void main(String[] args) {
        // Load the file to a Data object.
        Data data = DataManager.loadData("C:\\My Files\\LoadExampleFile.txt");

        // Example with how to use the data:

        // Iterate over all of the fragments the data contains.
        for(DataFragment fragment : data.getFragments()) {

            // Iterate over all of the objects the fragment contains.
            for(Object obj : fragment.getObjects()) {

                // Print them to console.
                System.out.println("Type: " + obj.getClass().getName() + ", Value: " + obj);
            }
        }
    }
}
