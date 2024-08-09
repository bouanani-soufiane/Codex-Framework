package com.codex.framework.EntityManager.utils;

import java.util.Scanner;

public class Print {
    public static <T> void log(T objectToPrint){
        System.out.println(objectToPrint);
    }
    public static String readString(String key , Scanner scanner){
        scanner.nextLine();
        System.out.print(key+" : ");
        return scanner.nextLine();
    }
}