package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import config.Evaluateur.TouchInfo;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe utilitaire pour la gestion des fichiers JSON.
 *
 * @param <K> Type de la cle.
 * @param <V> Type de la valeur.
 */
public class Jsonfile<K, V> {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Cree un fichier JSON à partir d'un objet.
     *
     * @param data     Donnees à ecrire.
     * @param filePath Chemin du fichier JSON.
     */
    public void create_json(Object data, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            System.out.println("Erreur lors de la creation du fichier JSON : " + filePath);
            e.printStackTrace();
        }
    }

    /**
     * Lit un fichier JSON et le convertit en une Map de String à Integer.
     *
     * @param filePath Chemin du fichier JSON.
     * @return Map contenant les donnees JSON ou null en cas d'erreur.
     */
    public static HashMap<String, Integer> readJsonAsMapStringInteger(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Type type = new TypeToken<HashMap<String, Integer>>() {
            }.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture du fichier JSON : " + filePath);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lit un fichier JSON et le convertit en une Map de String à Map de String à
     * Integer.
     *
     * @param filePath Chemin du fichier JSON.
     * @return Map contenant les donnees JSON ou null en cas d'erreur.
     */
    public static Map<String, Map<String, Integer>> readJsonAsMapStringMapStringInteger(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Type type = new TypeToken<Map<String, Map<String, Integer>>>() {
            }.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture du fichier JSON : " + filePath);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Charge la disposition du clavier à partir d'un fichier JSON.
     * Adapte pour la structure JSON avec la cle "disposition".
     *
     * @param filePath Chemin du fichier JSON de disposition.
     * @return Map contenant les informations de disposition ou null en cas
     *         d'erreur.
     */
    public static HashMap<Character, TouchInfo> loadDispositionFromJson(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            // Definir le type pour le JSON avec la cle "disposition"
            Type topLevelType = new TypeToken<Map<String, Map<String, TouchInfo>>>() {
            }.getType();
            Map<String, Map<String, TouchInfo>> topMap = gson.fromJson(reader, topLevelType);

            if (!topMap.containsKey("disposition")) {
                System.out.println("La cle 'disposition' est absente du fichier JSON.");
                return null;
            }

            Map<String, TouchInfo> dispoMap = topMap.get("disposition");
            HashMap<Character, TouchInfo> result = new HashMap<>();

            for (Map.Entry<String, TouchInfo> entry : dispoMap.entrySet()) {
                String key = entry.getKey();
                if (key.length() != 1) {
                    System.out.println("Cle de disposition invalide (doit etre un seul caractere) : " + key);
                    continue;
                }
                char c = key.charAt(0);
                result.put(c, entry.getValue());
            }

            return result;
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture du fichier de disposition du clavier : " + filePath);
            e.printStackTrace();
            return null;
        }
    }
    public static void createJsonFromDisposition(Map<Character, TouchInfo> disposition, String filePath) {
        Map<String, TouchInfo> dispoMap = new HashMap<>();
        for (Map.Entry<Character, TouchInfo> entry : disposition.entrySet()) {
            dispoMap.put(entry.getKey().toString(), entry.getValue());
        }

        Map<String, Map<String, TouchInfo>> topMap = new HashMap<>();
        topMap.put("disposition", dispoMap);

        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(topMap, writer);
        } catch (IOException e) {
            System.out.println("Erreur lors de la creation du fichier JSON de disposition du clavier : " + filePath);
            e.printStackTrace();
        }
    }
}
