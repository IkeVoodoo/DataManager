package me.ikevoodoo.data.loader;

/**
 * Exception thrown by the DataLoader when a corrupt instruction is found.
 * @see DataLoader
 */
public class CorruptDataException extends Exception {

    public CorruptDataException(String text) {
        super(text);
    }

}
