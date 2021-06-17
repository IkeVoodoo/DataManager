package me.ikevoodoo.datamanager.transform;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.ikevoodoo.datamanager.Data;
import me.ikevoodoo.datamanager.DataManager;
import me.ikevoodoo.datamanager.load.CorruptDataException;

import java.io.File;
import java.io.IOException;

public class DataTransformer {

    private final Gson gson;

    public DataTransformer() {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public DataTransformer(boolean prettyPrint) {
        GsonBuilder builder = new GsonBuilder();
        if(prettyPrint) builder.setPrettyPrinting();
        gson = builder.create();
    }

    public Data fromJson(String json) {
        return gson.fromJson(json, Data.class);
    }

    public String toJson(Data data) {
        return gson.toJson(data);
    }

    public static void main(String[] args) throws CorruptDataException, IOException {
        DataTransformer transformer = new DataTransformer();
        Data data = DataManager.loadData(new File("C:\\Users\\admin\\Desktop\\testload"));
        String json = transformer.toJson(data);
        System.out.println(json);

        Data reParsed = transformer.fromJson(json);
        System.out.println(reParsed.getFragments().get(0));
    }

}
