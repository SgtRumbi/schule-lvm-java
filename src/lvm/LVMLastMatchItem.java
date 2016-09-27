package lvm;

/**
 * Created by Johannes on 27.09.2016.
 */
public class LVMLastMatchItem {
    public final int key, value;

    LVMLastMatchItem(int key, int value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "{key: " + key + ", value: " + value + "}";
    }
}
