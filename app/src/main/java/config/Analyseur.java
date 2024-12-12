package config;

import java.util.HashMap;
import utils.Jsonfile;

public class Analyseur {

    String message;
    int nb_occurence;
    HashMap<String, Integer> map = new HashMap<String, Integer>();

    public Analyseur(String message, int i) {
        this.message = message;
        this.nb_occurence = i;
    }

    public String delete_space(String message) {
        return message.replaceAll("\\s", "");
    }

    public void analyse() {
        String occurence = "";
        map = new HashMap<String, Integer>();
        message = delete_space(message);
        for (int i = 0; i < message.length(); i++) {
            if (i + nb_occurence >= message.length()) {
                break;
            }
            occurence = message.substring(i, i + nb_occurence);

            if (map.containsKey(occurence)) {
                map.put(occurence, map.get(occurence) + 1);
            } else {
                map.put(occurence, 1);
            }
        }
    }

    public static void afficher(HashMap<String, Integer> map) {
        System.out.println("######");
        for (String key : map.keySet()) {
            System.out.println(key + " : " + map.get(key));
        }
        System.out.println("######");
    }

    public void create_jsonBis(String path) {
        Jsonfile<String, Integer> jsonfile = new Jsonfile<String, Integer>();
        jsonfile.create_json(map, path);
    }

    public HashMap<String, Integer> getMap() {
        return map;
    }

}
