package me.ikevoodoo.data.load;

import me.ikevoodoo.data.Data;
import me.ikevoodoo.data.DataFragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(input))) {
            String line;
            boolean isReadingFragment = false;
            DataFragment fragment = new DataFragment();
            List<Integer> ids = new ArrayList<>();
            int pos;
            int i = -1;

            while((line = bufferedReader.readLine()) != null) {
                i++;
                String l = line.stripLeading().stripTrailing();
                int x = 0;
                char prev = 0;
                for(char c : l.toCharArray()) {
                    if(c == comment && prev != '\\') {
                        l = l.substring(0, Math.max(x - 1, 0));
                        break;
                    }
                    prev = c;
                    x++;
                }
                if(l.isBlank()) continue;

                if(l.startsWith(fragStart)) {
                    if(isReadingFragment) continue;
                    isReadingFragment = true;
                    if(l.stripTrailing().length() == fragStart.length()) {
                        fragment.name = "UNNAMED_FRAGMENT";
                        fragment.id = -1;
                        continue;
                    }
                    String data = l.substring(fragStart.length() + 1).stripLeading();
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

                        String name = l.substring(fragStart.length() + 1 + unprocessedID.length() + 1);
                        if(name.isBlank())
                            fragment.name = "UNNAMED_FRAGMENT";
                        else
                            fragment.name = name;
                    } else {
                        fragment.name = "UNNAMED_FRAGMENT";
                        fragment.id = -1;
                    }
                }

                if(l.startsWith(fragEnd)) {
                    if(!isReadingFragment) continue;
                    isReadingFragment = false;
                    if(fragment.getObjects().size() > 0)
                        out.getFragments().add(fragment);

                    fragment = new DataFragment();
                }

                if(isReadingFragment)
                    parse(l, fragment, i);
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
