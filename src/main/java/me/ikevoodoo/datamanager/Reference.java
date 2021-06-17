package me.ikevoodoo.datamanager;

import java.util.*;

public class Reference {

    private final int index;
    private final List<Object> objects = new ArrayList<>();
    private static final HashMap<Integer, Reference> references = new HashMap<>();

    private void addAll(List<Object> obj, List<Object> objs) {
        objs.forEach(o -> {
            if(o.getClass().isArray())
                addAll(obj, Arrays.asList((Object[])o));
            else obj.add(o);
        });
    }

    public Reference(int index, Object... objects) {
        addAll(this.objects, Arrays.asList(objects));
        this.index = index;
        Reference.references.put(index, this);
    }

    public int getIndex() {
        return this.index;
    }

    public static Reference getReferenceAt(int id) {
        return Reference.references.get(id);
    }

    public List<Object> getObjects() {
        return this.objects;
    }

    @Override
    public String toString() {
        return "Length: " + objects.size() + ", Index: " + this.index;
    }

}