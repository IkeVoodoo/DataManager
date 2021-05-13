package me.ikevoodoo.datamanager;

import me.ikevoodoo.datamanager.load.DataLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Container class for DataFragment, returned by {@link DataLoader#loadData(File)}
 * 
 * @see DataFragment
 * @see DataLoader#loadData(File) 
 */
@SuppressWarnings("unused")
public class Data {

    /**
     * Indicates if the data is corrupt.
     */
    public boolean corrupt;

    private final List<DataFragment> fragments = new ArrayList<>();

    /**
     * Get the contained DataFragment objects.
     * @return The DataFragment objects this object contains.
     * @see DataFragment
     */
    public List<DataFragment> getFragments() {
        return this.fragments;
    }

    public void addFragment(DataFragment fragment) { this.fragments.add(fragment); }

    /**
     * Gets the fragment with specified id.
     * @param id The id of the fragment.
     * @return The fragment with the corresponding id, otherwise null if not found.
     */
    public DataFragment getFragmentById(int id) {
        DataFragment[] fragment = new DataFragment[1];
        fragments.forEach(f -> {
            if(fragment[0] != null)
                return;

            if(f.id == id)
                fragment[0] = f;
        });
        return fragment[0];
    }

    /**
     * Gets all of the fragments with the specified name.
     * @param name The name of the fragments you wish to find.
     * @return The list of fragments with the specified name, or a empty list if none are found.
     */
    public List<DataFragment> getFragmentsByName(String name) {
        List<DataFragment> dataFragments = new ArrayList<>();
        fragments.forEach(fragment -> {
            if(fragment.name.equalsIgnoreCase(name))
                dataFragments.add(fragment);
        });
        return dataFragments;
    }

    /**
     * Should be noted that {@link Data#getFragmentById(int)} is better<br>
     * as the fragment may not necessarily have a name.
     * @param name The name of which fragment you wish to find.
     * @return The first occurrence of a fragment with the specified name, null if not found.
     */
    public DataFragment getFragmentByName(String name) {
        DataFragment[] fragment = new DataFragment[1];
        fragments.forEach(f -> {
            if(fragment[0] != null)
                return;

            if(f.name.equalsIgnoreCase(name))
                fragment[0] = f;
        });
        return fragment[0];
    }

}
