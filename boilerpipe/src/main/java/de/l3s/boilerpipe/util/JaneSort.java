package de.l3s.boilerpipe.util;

import de.l3s.boilerpipe.document.TextBlock;
import de.l3s.boilerpipe.sax.HTMLHighlighter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 6/9/11
 * Time: 12:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class JaneSort {
    public static List<String> findBetween(Map<Integer, String> map, List<HTMLHighlighter.Block> list, int range) {

        List<String> result = new ArrayList<String>();

        Integer[] keyArray = map.keySet().toArray(new Integer[0]);

        int param = range;
        for (int j = 0; j < list.size(); j++) {
            int min = list.get(j).start - param;
            int max = list.get(j).end + param;
            int length = keyArray.length;

            for (int i = 0; i < length; i++) {
                if (keyArray[i] >= min && keyArray[i] <= max) {
                    result.add(map.get(keyArray[i]));
                }
            }
        }
        return result;
    }
}
