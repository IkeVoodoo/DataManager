package me.ikevoodoo.datamanager;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for fragment data, usually found in a Data object.
 *
 * @see Data
 */
public class DataFragment {

    /**
     * The name of the fragment, if no name was provided will default to "UNNAMED_FRAGMENT"
     */
    public String name = "UNNAMED_FRAGMENT";

    /**
     * The id of this fragment, if no ID was provided will default to -1
     */
    public int id = -1 ;

    private final List<Object> objects = new ArrayList<>();

    /**
     * Get the data contained within this fragment.<br>
     * @return The objects contained within this fragment.
     */
    public List<Object> getObjects() {
        return this.objects;
    }

    public void addObject(Object o) { this.objects.add(o); }

    public String toString() {
        return "Name: " + this.name + ", ID: " + this.id;
    }

}
