package me.ikevoodoo.datamanager.load;

import me.ikevoodoo.datamanager.Data;
import me.ikevoodoo.datamanager.DataFragment;
import me.ikevoodoo.datamanager.api.CustomType;
import me.ikevoodoo.datamanager.api.TypeRegistry;
import me.ikevoodoo.datamanager.load.expressions.ExpressionCalculator;

import java.util.Optional;

public class LoaderManager {

    private static final ExpressionCalculator expressionCalculator = new ExpressionCalculator() {
        @Override
        public int evaluate(String expr) {
            return ExpressionCalculator.super.evaluate(expr);
        }
    };

    private static DataFragment getDataFragment(Data data, String name, int line) throws CorruptDataException {
        Optional<DataFragment> fragment = data.getFragmentByName(name);

        if(fragment.isEmpty())
            throw new CorruptDataException("Trying to get non-existent fragment. Line: " + line);
        return fragment.get();
    }

    protected static void handleMerge(String line, int i, boolean shouldParseExpressions, Data out) throws CorruptDataException {
        int pos;
        String[] args = line.substring(line.indexOf(' ') + 1).split("\\s+");
        if(args.length == 2) {
            if((pos = args[0].indexOf(':')) != -1) {
                DataFragment sourceFragment = getDataFragment(out, args[0].substring(0, pos), i);
                String rawNumber = args[0].substring(pos + 1);
                int sourceDataPosition = shouldParseExpressions
                        ? expressionCalculator.evaluate(rawNumber)
                            : Integer.parseInt(rawNumber);


                if((pos = args[1].indexOf(':')) != -1) {
                    DataFragment destinationFragment = getDataFragment(out, args[1].substring(0, pos), i);
                    rawNumber = args[1].substring(pos + 1);
                    int targetDataPosition = shouldParseExpressions
                            ? expressionCalculator.evaluate(rawNumber)
                            : Integer.parseInt(rawNumber);
                    merge(sourceFragment, destinationFragment,
                            sourceDataPosition, targetDataPosition,
                                TypeRegistry.getTypeName(sourceFragment.getObjects().get(sourceDataPosition).getClass()));
                }
            }
        }
    }

    private static void merge(DataFragment src, DataFragment dest, int srcIndex, int destIndex, String typeName) {
        CustomType type = TypeRegistry.get(typeName);
        if(type != null)
            src.getObjects().set(srcIndex, type.merge(dest.getObjects().get(srcIndex), src.getObjects().get(destIndex)));
    }

}
