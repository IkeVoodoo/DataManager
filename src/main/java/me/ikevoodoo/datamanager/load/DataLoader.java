package me.ikevoodoo.datamanager.load;

import me.ikevoodoo.datamanager.Data;
import me.ikevoodoo.datamanager.DataFragment;
import me.ikevoodoo.datamanager.Reference;
import me.ikevoodoo.datamanager.api.CustomType;
import me.ikevoodoo.datamanager.api.TypeRegistry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class DataLoader {

    private static final String fragStart = ">FRAG", fragEnd = ">END", link = ">LINK", reference = ">REF",
            merge = ">MERGE";
    private static final char comment = '#';
    private static boolean inMultiComment = false, shouldCN = false;
    private static final HashMap<String, Object> options = new HashMap<>();

    static {
        options.put("-ParseExpressions", true);
        options.put("-RecursiveLinking", true);
        options.put("-ReferenceCopy", false);
        fixRegistry();
    }

    public static void fixRegistry() {
        TypeRegistry.addDefaultType("int", new CustomType() {

            @Override
            public Object handle(String typeName, String[] args, String combinedArgs) {
                if(options.get("-ParseExpressions") == Boolean.TRUE)
                    return evaluate(combinedArgs);
                else
                    return Integer.parseInt(combinedArgs);
            }

            @Override
            public Object merge(Object dest, Object src) {
                if(dest instanceof Integer && src instanceof Integer)
                    return (Integer) dest + (Integer) src;
                return dest;
            }
        }, Integer.class);

        TypeRegistry.addDefaultType("str", new CustomType() {

            @Override
            public Object handle(String typeName, String[] args, String combinedArgs) {
                return combinedArgs;
            }

            @Override
            public Object merge(Object dest, Object src) {
                return src.toString() + dest;
            }
        }, String.class);
        TypeRegistry.addDefaultType("bool", new CustomType() {

            @Override
            public Object handle(String typeName, String[] args, String combinedArgs) {
                return combinedArgs.trim().equalsIgnoreCase("true");
            }

            @Override
            public Object merge(Object dest, Object src) {
                if(dest instanceof Boolean && src instanceof Boolean)
                    return (Boolean) dest && (Boolean) src;
                return dest;
            }
        }, Boolean.class);
    }


    /**
     * Set the state of an option, this will later be implemented as a keyword.
     */
    public void setOption(String option, Object value) {
        Object val;
        if((val = options.get(option)) != null) {
            if(val.getClass() == value.getClass())
                options.put(option, value);
            else throw new IllegalArgumentException("Tried to pass different type of argument to option.");
        } else throw new IllegalArgumentException("The option you are trying to set does not exist! Option: " + option);
    }

    public Class<?> getOptionType(String option) {
        Object val;
        if((val = options.get(option)) != null)
            return val.getClass();

        return null;
    }

    public Object getOption(String option) {
        return options.get(option);
    }

    private List<String> chainUnwrap(File input) {
        List<String> out = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(input))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.stripLeading();
                if (line.startsWith(link)) {
                    String path = line.substring(link.length()).stripLeading().stripTrailing();
                    String curr = input.getParentFile().getPath();
                    if (!path.startsWith("/") || !path.startsWith("\\"))
                        path = "/" + path;
                    path = curr + path;
                    out.addAll(chainUnwrap(new File(path)));
                } else out.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    private List<String> unwrap(File input) throws IOException  {
        List<String> lines = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(input))) {
            String line;
            boolean recursive = options.get("-RecursiveLinking") == Boolean.TRUE;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.stripLeading();
                if (line.startsWith(link)) {
                    String path = line.substring(link.length()).stripLeading().stripTrailing();
                    String curr = input.getParentFile().getPath();
                    if (!path.startsWith("/") || !path.startsWith("\\"))
                        path = "/".concat(path);
                    path = curr + path;
                    lines.addAll(recursive ? chainUnwrap(new File(path)) :
                            Files.readAllLines(Path.of(path)));
                } else
                    lines.add(line);
            }
        }
        return lines;
    }

    private String parseLine(String line) {
        int x = 0;
        char prev = 0;
        boolean shouldC = true;
        final String[] split = line.stripTrailing().substring(line.indexOf(' ') + 1).split("\\s+");
        if(line.toLowerCase(Locale.ROOT).startsWith("#define")) {
            if(split.length == 2) {
                if(TypeRegistry.isRegistered(split[0])) {
                    TypeRegistry.setAlias(split[0], split[1]);
                    shouldC = false;
                }
            }
        } else if(line.stripTrailing().toLowerCase(Locale.ROOT).startsWith("#undefine")) {
            String toUndefine = split[0];
            if(TypeRegistry.hasAlias(toUndefine)) {
                TypeRegistry.removeAlias(toUndefine);
                shouldC = false;
            }
        } else if(line.toLowerCase(Locale.ROOT).startsWith("#param")) {
            for(String s : split) {
                int i = s.indexOf(':');
                if(i == -1) continue;
                String key = s.substring(0, i);
                String val = s.substring(i + 1);
                Class<?> keyType = getOptionType(key);
                if(keyType == null) continue;
                if(keyType == Boolean.class)
                    setOption(key, val.equalsIgnoreCase("true"));
            }
        } else if(line.stripLeading().startsWith("#*"))
            inMultiComment = true;
        else if(line.endsWith("#*"))
            inMultiComment = shouldCN = true;
        else if(line.stripLeading().startsWith("*#")) {
            inMultiComment = false;
            return "";
        }
        if(shouldC) inMultiComment = false;
        if(shouldCN) {
            shouldCN = false;
            return line.substring(0, line.length() - 2);
        }
        if(inMultiComment) return "";
        for (char c : line.toCharArray()) {
            if(!shouldC) break;
            if (c == comment && prev != '\\')
                return line.substring(0, Math.max(x - 1, 0)).stripTrailing();
            prev = c;
            x++;
        }
        return line;
    }

    private DataFragment loadFragment(String line, Iterator<String> iterator, int i, Data out)
            throws CorruptDataException {
        DataFragment fragment = new DataFragment();
        int pos;
        List<String> names = new ArrayList<>();
        while(iterator.hasNext()) {
            i++;
            if(line.isBlank() || line.isEmpty()) {
                line = parseLine(iterator.next().stripLeading());
                continue;
            }

            if (line.startsWith(fragStart)) {
                if (line.length() == fragStart.length())
                    throw new CorruptDataException("Cannot define unnamed fragment! Line: " + i);

                String name = line.substring(fragStart.length() + 1).stripLeading();
                if (name.isBlank())
                    throw new CorruptDataException("Cannot find fragment name at line: " + i);


                if (names.contains(name)) {
                    throw new CorruptDataException("Trying to create a sub fragment with a name that is already in use! Line: " + i
                            + " ".repeat(line.length()) + "^ HERE");
                }
                DataFragment frag = loadFragment(parseLine(iterator.next().stripLeading()), iterator, i, out);
                names.add(frag.name = name);
                frag.name = name;
                frag.setParent(fragment);
                fragment.addObject(frag);
            }

            else if (line.startsWith(reference)) {
                if (line.length() == reference.length())
                    throw new IllegalArgumentException("Invalid reference at line: " + i);

                String data = line.substring(reference.length() + 1).stripLeading();
                if (data.isBlank())
                    throw new CorruptDataException("Cannot attempt to reference at a blank fragment. Line: " + i);
                String fragName = data.stripLeading().stripTrailing();
                Optional<DataFragment> ref = out.getFragmentByName(fragName);
                pos = data.indexOf(':');

                if(pos == -1 && ref.isEmpty())
                    ref = Optional.ofNullable(fragment.getChildFragment(fragName));

                if(ref.isEmpty() && fragName.equals(fragment.name))
                    ref = Optional.of(fragment);

                boolean refC = options.get("-ReferenceCopy") == Boolean.TRUE;
                if (pos != -1) {
                    fragName = data.substring(0, pos).stripLeading().stripTrailing();
                    ref = out.getFragmentByName(fragName);
                    if(ref.isEmpty())
                        ref = Optional.ofNullable(fragment.getChildFragment(fragName));
                    if(ref.isEmpty() && fragName.equals(fragment.name))
                        ref = Optional.of(fragment);


                    int valToGet = Integer.parseInt(data.substring(fragName.length() + 1).trim());
                    if(ref.isEmpty())
                        throw new CorruptDataException("Cannot attempt to reference at non-existing fragment! Line: " + i);
                    if (valToGet > ref.get().getObjects().size())
                        throw new IndexOutOfBoundsException("Attempting to get value outside of fragment length. Line: " + i);
                    if (valToGet < 0)
                        throw new IndexOutOfBoundsException("Trying to get value under 0. Line: " + i);

                    Object obj = ref.get().getObjects().get(valToGet);
                    if(obj instanceof Reference) {
                        Reference refR = (Reference) obj;
                        if(refR.getObjects().size() > 0) {
                            fragment.getObjects().add(new Reference(i, refC ? refR.getObjects().toArray() : refR));
                        }
                    } else fragment.getObjects().add(new Reference(i, ref.get().getObjects().get(valToGet)));
                } else {
                    if(ref.isEmpty())
                        throw new CorruptDataException("Cannot attempt to reference at non-existing fragment! Line: " + i);

                    List<Object> objs = ref.get().getObjects();
                    int finalI = i;
                    Reference refRF = new Reference(i);
                    objs.forEach(obj -> {
                        if(obj instanceof Reference) {
                            Reference refR = (Reference) obj;
                            if(refR.getObjects().size() > 0)
                                refRF.getObjects().add(new Reference(finalI, refC ? refR.getObjects().toArray() : refR));
                        } else refRF.getObjects().add(obj);
                    });
                    if(refRF.getObjects().size() > 0)
                        fragment.getObjects().add(refRF);
                }
            }

            else if (line.startsWith(fragEnd)) {
                if (fragment.getObjects().size() > 0)
                    return fragment;
                break;
            }
            else
                parse(line, fragment, i);
            line = parseLine(iterator.next().stripLeading());
        }
        return fragment;
    }

    /**
     * Creates a data object from the instructions found in the provided file.
     *
     * @return The parsed data from the specified file.
     * Otherwise if no instructions are found, will return a empty Data object.
     * @see Data
     * @see File
     */
    public Data loadData(File input) throws IllegalArgumentException, CorruptDataException, IOException {
        Data out = new Data();
        out.corrupt = false;
        int i = -1;
        List<String> names = new ArrayList<>();
        try {
            for (Iterator<String> iterator = unwrap(input).iterator(); iterator.hasNext();) {
                i++;
                String line = parseLine(iterator.next().stripLeading());

                if (line.isBlank()) continue;
                if(line.startsWith(merge))
                    LoaderManager.handleMerge(line, i,
                            options.get("-ParseExpressions") == Boolean.TRUE, out);


                if (line.startsWith(fragStart)) {
                    String name = line.substring(fragStart.length() + 1).stripLeading();
                    if(names.contains(name))
                        throw new CorruptDataException("Trying to create a fragment with a name that is already in use! Line: " + i);
                    DataFragment frag = loadFragment(parseLine(iterator.next().stripLeading()), iterator, i, out);
                    names.add(frag.name = name);
                    out.addFragment(frag);
                }
            }
        } catch (Exception e) {
            if(e instanceof CorruptDataException)
                out.corrupt = true;
            throw e;
        }
        return out;
    }


    private String getInstructionValue(String l) {
        int pos;
        if ((pos = l.indexOf(':')) != -1) {
            String s = l.substring(pos + 1).stripLeading();
            if (!s.isBlank())
                return s;
            else return "#BLANK_DATA";
        } else return "#INV_DATA";
    }

    private void parse(String l, DataFragment fragment, int i) throws CorruptDataException {
        String val = getInstructionValue(l);
        if (val.equalsIgnoreCase("#INV_DATA"))
            throw new CorruptDataException("Invalid instruction at line: " + i + " (ERR_INV_DATA)");
        else if (val.equalsIgnoreCase("#BLANK_DATA"))
            throw new CorruptDataException("Invalid instruction at line: " + i + " (ERR_BLANK_DATA)");

        int pos = l.indexOf(':');
        String typeName = l.substring(0, pos);

        CustomType type;
        if((type = TypeRegistry.get(typeName)) != null)
            fragment.getObjects().add(type.handle(typeName, val.split("\\s*"), val));
        else throw new CorruptDataException("Invalid type name found: " + typeName + ", at line: " + i);

    }

}
