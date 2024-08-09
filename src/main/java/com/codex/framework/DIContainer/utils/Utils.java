package com.codex.framework.DIContainer.utils;

import java.util.Map;

public class Utils {
    public static void printHashMap( Map<?, ?> map) {
        if (map == null || map.isEmpty()) {
            System.out.println("The map is empty or null.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("HashMap Contents:\n");
        sb.append("-----------------\n");

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            sb.append("Key: ").append(entry.getKey())
                    .append(", Value: ").append(entry.getValue())
                    .append("\n");
        }

        System.out.println(sb.toString());
    }
}
