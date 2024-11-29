package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Readfile {

    public static String readFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    public static void main(String[] args) {
        System.out.println(readFile("app/src/main/java/config/Analyseur.java"));
    }

}
