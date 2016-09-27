package lvm;

import java.util.Arrays;

/**
 * Created by Johannes on 27.09.2016.
 */
public class LVMResult {
    public final int finalSum, finalTolerance;
    public final LVMLastMatchItem[] lastMatch;
    public final int[] backupBaseArray;

    LVMResult(int finalSum, int finalTolerance, LVMLastMatchItem[] lastMatch, int[] backupBaseArray) {
        this.finalSum = finalSum;
        this.finalTolerance = finalTolerance;
        this.lastMatch = lastMatch;
        this.backupBaseArray = backupBaseArray;
    }

    @Override
    public String toString() {
        String string = "";
        string += "finalSum: " + finalSum + "\n";
        string += "finalTolerance: " + finalTolerance + "\n";
        string += "lastMatch: " + Arrays.toString(lastMatch);
        return string;
    }
}
