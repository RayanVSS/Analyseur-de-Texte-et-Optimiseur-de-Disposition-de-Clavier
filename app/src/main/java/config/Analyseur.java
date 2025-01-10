package config;

import java.util.HashMap;
import utils.Jsonfile;

/**
 * Classe pour analyser la frequence des suites de caracteres.
 */
public class Analyseur {

    String message;
    int nb_occurence;
    HashMap<String, Integer> map = new HashMap<String, Integer>();

    public Analyseur(String message, int i) {
        this.message = message;
        this.nb_occurence = i;
    }

    /**
     * Supprime les espaces dans le message.
     *
     * @param message Texte à nettoyer.
     * @return Texte sans espaces.
     */
    public String delete_space(String message) {
        return message.replaceAll("\\s", "");
    }

    /**
     * Analyse la frequence des suites de caracteres dans le contenu.
     */
    public void analyse() {
        String occurence = "";
        map = new HashMap<String, Integer>();
        message = delete_space(message);
        for (int i = 0; i < message.length(); i++) {
            if (i + nb_occurence > message.length()) {
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

    /**
     * Affiche la map de frequence.
     *
     * @param map Map de frequence à afficher.
     */
    public void afficher(HashMap<String, Integer> map) {
        System.out.println("######");
        for (String key : map.keySet()) {
            System.out.println(key + " : " + map.get(key));
        }
        System.out.println("######");
    }

    /**
     * Cree un fichier JSON à partir de la map de frequence.
     *
     * @param path Chemin du fichier JSON.
     */
    public void create_jsonBis(String path) {
        Jsonfile<String, Integer> jsonfile = new Jsonfile<String, Integer>();
        jsonfile.create_json(map, path);
    }

    /**
     * Retourne la map de frequence.
     *
     * @return Map de frequence des suites de caracteres.
     */
    public HashMap<String, Integer> getMap() {
        return map;
    }

}
