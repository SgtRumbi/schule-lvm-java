package lvm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johannes on 27.09.2016.
 */
public class LVM {
    public static LVMResult lvmCalculate(final int[] baseArray, final int requestedPackageSize) {
        boolean foundMatch = false;
        int tolerance = 0;
        int greatestMatch = 0;
        int finalSum = 0;
        final List<Integer> lastMatchesList = new ArrayList<>();
        int sum = 0;

        // TODO: 27.09.2016 Add nextToReqPckgSize!
        do {
            for (int possibility = 0; possibility < Math.pow(2, baseArray.length); possibility++) {
                sum = 0;
                // Last match ++
                // int lastMatchCounter = 0;
                // lastMatch = new int[baseArray.length];
                lastMatchesList.clear();
                for (int elementIndex = 0; elementIndex < baseArray.length; elementIndex++) {
                    int possibilityAndElementIndexPos = possibility & (int) Math.pow(2, elementIndex);

                    if (possibilityAndElementIndexPos != 0) {
                        // Add elementIndex to lastMatch
                        lastMatchesList.add(elementIndex);
                        // lastMatch[lastMatchCounter++] = elementIndex;

                        // If is matching, add value of baseArray[elementIndex]
                        sum += baseArray[elementIndex];
                    }
                }

                if (sum > greatestMatch) {
                    greatestMatch = sum;
                }

                // Sum is best possible value
                if (sum == requestedPackageSize + tolerance) {
                    finalSum = sum;
                    // Escape while
                    foundMatch = true;
                    // Break for
                    break;
                }
            }

            // If there is no match greater requestedPackageSize, the best
            // match is the greatest match
            if (greatestMatch < requestedPackageSize) {
                finalSum = sum;
                // Escape while in next cycle
                break;
            }

            // If program runs until here, increase tolerance, there is
            // no good match or no match found
            if (!foundMatch) {
                tolerance++;
            }
        }
        // Try to find match while there is no match
        while (!foundMatch);

        final LVMLastMatchItem[] lastMatchesArray = new LVMLastMatchItem[lastMatchesList.size()];
        for (int i = 0; i < lastMatchesArray.length; i++) {
            int lastMatchIndex = lastMatchesList.get(i);
            lastMatchesArray[i] = new LVMLastMatchItem(lastMatchIndex, baseArray[lastMatchIndex]);
        }

        return new LVMResult(finalSum, tolerance, lastMatchesArray, baseArray);
    }
}
