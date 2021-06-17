package me.ikevoodoo.datamanager;

import me.ikevoodoo.datamanager.load.DataLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
     *
     * @param name The name of which fragment you wish to find.
     * @return The first occurrence of a fragment with the specified name, null if not found.
     */
    public Optional<DataFragment> getFragmentByName(String name) {
        return fragments.stream().filter(f -> f.name.equalsIgnoreCase(name)).findFirst();
    }

}
