package me.ikevoodoo.datamanager.api;

import me.ikevoodoo.datamanager.load.DataLoader;

import java.util.HashMap;

/**
 * The registry that keeps track of CustomTypes, Aliases and TypeNames
 * @see CustomType
 */
public class TypeRegistry {

    private static final HashMap<String, CustomType> types = new HashMap<>();
    private static final HashMap<String, String> aliases = new HashMap<>();
    private static final HashMap<Class<?>, String> typeNames = new HashMap<>();

    /**
     * Register your own custom type.
     * <br>
     * Note: When sharing the files you HAVE to give them the same types that you registered, otherwise loading or saving will fail.
     * @param name The name that your type  will be identified as
     * @param type The CustomType object that will handle this type
     * @param returnType The class of the object that the type returns upon requested.
     * @throws IllegalArgumentException If the type is already registered.
     * @see CustomType
     */
    public static void addType(String name, CustomType type, Class<?> returnType) throws IllegalArgumentException {
        if(!types.containsKey(name) && !typeNames.containsKey(returnType)) {
            types.put(name, type);
            typeNames.put(returnType, name);
        }
        else throw new IllegalArgumentException("Cannot try to add already existing type!");
    }

    /**
     * Used by the DataLoader to initialize default types.
     */
    public static void addDefaultType(String name, CustomType type, Class<?> returnType) throws IllegalArgumentException {
        if(StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass() != DataLoader.class)
            throw new IllegalCallerException("Only the DataLoader class can call this method.");
        if(!types.containsKey(name) && !typeNames.containsKey(returnType)) {
            types.put(name, type);
            typeNames.put(returnType, name);
        }
        else throw new IllegalArgumentException("Cannot try to add already existing type!");
    }


    /**
     * Set an alias for a CustomType. Can be called as a keyword using #define [TypeName] [Alias]
     * @param name The name of the type you wish to add an alias to.
     * @param alias The alias for the targeted type.
     * @see CustomType
     */
    public static void setAlias(String name, String alias) {
        aliases.put(alias, name);
    }

    /**
     * Removes an alias from a data type. Can be called as a keyword using #undefine [Alias]
     * @param alias The alias to remove
     */
    public static void removeAlias(String alias) {
        aliases.remove(alias);
    }

    /**
     * Fetches the TypeName from a class.
     * @param returnType The class you wish to get the TypeName from.
     * @return The type name associated with the provided class, otherwise null if not found.
     */
    public static String getTypeName(Class<?> returnType) {
        return typeNames.get(returnType);
    }

    /**
     * Checks wherever the CustomType with provided name exists.
     * @param name The name you wish to check if is registered.
     * @return Wherever the provided name is associated with a CustomType
     */
    public static boolean isRegistered(String name) {
        return types.containsKey(name);
    }

    /**
     * Checks wherever the provided alias is registered.
     * @param alias The alias you wish to check.
     * @return Wherever the alias has been registered.
     */
    public static boolean hasAlias(String alias) {
        return aliases.containsKey(alias);
    }

    /**
     * Get a CustomType based from the provided name.
     * @param name The name of the CustomType you wish to get.
     * @return The custom type associated with said name, otherwise null if not found.
     * @see CustomType
     */
    public static CustomType get(String name) {
        CustomType ret;
        if((ret = types.get(name)) != null)
            return ret;
        else
            return types.get(aliases.get(name));
    }

}
