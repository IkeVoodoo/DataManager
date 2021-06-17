package me.ikevoodoo.datamanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

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

    private Object parent;

    private final List<Object> objects = new ArrayList<>();

    /**
     * Get the data contained within this fragment.<br>
     * @return The objects contained within this fragment.
     */
    public List<Object> getObjects() {
        return this.objects;
    }

    public void setParent(Object obj) {
        if(parent != null) return;
        if(obj == null) return;
        this.parent = obj;
    }

    public DataFragment getChildFragment(String name) {
        Optional<Object> obj = objects.stream().filter(f -> f instanceof DataFragment &&
                ((DataFragment)f).name.equalsIgnoreCase(name)).findFirst();
        if(obj.isPresent() && obj.get() instanceof DataFragment)
            return (DataFragment) obj.get();
        return null;
    }

    public Object getParent() { return parent; }

    public void addObject(Object o) { this.objects.add(o); }

    public String toString() {
        return "Name: " + this.name;
    }

}
