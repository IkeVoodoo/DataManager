package me.ikevoodoo.datamanager.load;

import me.ikevoodoo.datamanager.Data;
import me.ikevoodoo.datamanager.DataFragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {

    private static final String fragStart = ">FRAG", fragEnd = ">END";
    private static final char comment = '#';

    /**
     * Creates a data object from the instructions found in the provided file.
     * @return The parsed data from the specified file.
     *         Otherwise if no instructions are found, will return a empty Data object.
     * @see Data
     * @see File
     */
    public Data loadData(File input) {
        Data out = new Data();
        out.corrupt = false;
        List<Integer> ids = new ArrayList<>();
        int pos;
        int i = -1;
        String line;
        boolean isReadingFragment = false;
        DataFragment fragment = new DataFragment();
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(input))) {
            while((line = bufferedReader.readLine()) != null) {
                i++;
                line =  line.stripLeading().stripTrailing();
                int x = 0;
                char prev = 0;
                for(char c : line.toCharArray()) {
                    if(c == comment && prev != '\\') {
                        line = line.substring(0, Math.max(x - 1, 0)).stripTrailing();
                        break;
                    }
                    prev = c;
                    x++;
                }
                if(line.isBlank()) continue;

                if(line.startsWith(fragStart)) {
                    if(isReadingFragment) continue;
                    if(line.length() == fragStart.length()) {
                        fragment.name = "UNNAMED_FRAGMENT";
                        fragment.id = -1;
                        continue;
                    }
                    isReadingFragment = true;
                    String data = line.substring(fragStart.length() + 1).stripLeading();
                    if((pos = data.indexOf(' ')) != -1) {
                        String unprocessedID = data.substring(0, pos);
                        if (unprocessedID.isBlank())
                            fragment.id = -1;
                        else {
                            try {
                                fragment.id = Integer.parseInt(unprocessedID);
                                if(fragment.id < 0) {
                                    fragment.id = -fragment.id;
                                    System.err.println("Reset number to positive at line: " + i);
                                }
                                if(ids.contains(fragment.id))
                                    throw new CorruptDataException("Cannot have two fragments with the same id, at line: " + i);
                                ids.add(fragment.id);
                            } catch (NumberFormatException e) {
                                System.err.println("COULD NOT PARSE ID: " + unprocessedID);
                                fragment.id = -1;
                            }
                        }

                        String name = line.substring(fragStart.length() + 1 + unprocessedID.length() + 1);
                        if(name.isBlank())
                            fragment.name = "UNNAMED_FRAGMENT";
                        else
                            fragment.name = name;
                    } else {
                        fragment.name = "UNNAMED_FRAGMENT";
                        fragment.id = -1;
                    }
                }

                if(line.startsWith(fragEnd)) {
                    if(!isReadingFragment) continue;
                    isReadingFragment = false;
                    if(fragment.getObjects().size() > 0)
                        out.getFragments().add(fragment);

                    fragment = new DataFragment();
                }

                if(isReadingFragment)
                    parse(line, fragment, i);
            }
        } catch (Exception e) {
            if(e instanceof CorruptDataException)
                out.corrupt = true;
            e.printStackTrace();
        }
        return out;
    }

    private void parse(String l, DataFragment fragment, int i) throws Exception {
        int pos;
        if(l.startsWith("str")) {
            if((pos = l.indexOf(':')) != -1) {
                String s = l.substring(pos + 1).stripLeading();
                if(!s.isBlank()) {
                    fragment.getObjects().add(s);
                }
            } else
                throw new CorruptDataException("Invalid line at position: " + i + ", line: '" + l + "'");
        } else if(l.startsWith("int")) {
            if((pos = l.indexOf(':')) != -1) {
                String s = l.substring(pos + 1).stripLeading();
                if(!s.isBlank()) {
                    try {
                        fragment.getObjects().add(Integer.parseInt(s));
                    } catch (NumberFormatException e) {
                        System.err.println("COULD NOT PARSE INTEGER: " + s);
                    }
                }
            } else
                throw new CorruptDataException("Invalid line at position: " + i + ", line: '" + l + "'");
        } else if(l.startsWith("bool")) {
            if((pos = l.indexOf(':')) != -1) {
                String s = l.substring(pos + 1).stripLeading();
                if(!s.isBlank())
                    fragment.getObjects().add(s.equalsIgnoreCase("true"));
            } else
                throw new CorruptDataException("Invalid line at position: " + i + ", line: '" + l + "'");
        }
    }

}
