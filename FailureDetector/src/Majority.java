import java.util.HashMap;
import java.util.Map;

public class Majority {
    public static Integer calc(Integer [] array) {
        Map<Integer, Integer> count = new HashMap<Integer, Integer>();

        // Count the number of occurrences of each value in the array and populate map
        for (Integer number : array) {
            if (count.containsKey(number)) {
                count.put(number, count.get(number) + 1);
            } else {
                count.put(number, new Integer (1));
            }
        }

        // Find the majority value
        Integer majority = null;
        for (Integer key : count.keySet()) {
            if (count.get(key) > array.length / 2) {
                majority = key;
                break;
            }
        }

        return majority;
    }
}