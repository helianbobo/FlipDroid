package it.tika;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Test {
    private final String a;

    public Test() {
        a = "a";
    }

    public static void main(String[] args) {
        List<String> a = new ArrayList<String>();
        a.add("1");
        a.add("2");
        a.add("3");

        permutation(a, 0, a.size() - 1);

    }

    private static void permutation(List<String> list, int start, int end) {
        System.out.println(list);
        if (start == list.size() - 1) {
            return;
        } else {
            String tmp = list.get(start + 1);
            list.set(start + 1, list.get(start));
            list.set(start, tmp);
            for (int i = end; i > 0; i--) {
                permutation(list, start + 1, i);
            }
        }
    }
}