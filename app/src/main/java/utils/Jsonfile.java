package utils;

import java.util.HashMap;
import java.util.Map;
import java.io.FileWriter;
import java.io.IOException;
import org.json.simple.JSONObject;
import java.io.FileReader;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import config.Evaluateur;

import java.lang.reflect.Type;

public class Jsonfile<T1, T2> {

    public void create_json(HashMap<T1, T2> map, String path) {
        JSONObject obj = new JSONObject();
        for (T1 key : map.keySet()) {
            obj.put(String.valueOf(key), String.valueOf(map.get(key)));
        }
        try (FileWriter file = new FileWriter(path)) {
            file.write(obj.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<T1, T2> read_json(String path) {
        JSONParser parser = new JSONParser();
        HashMap<T1, T2> map = new HashMap<T1, T2>();
        try {
            Object obj = parser.parse(new FileReader(path));
            JSONObject jsonObject = (JSONObject) obj;
            for (Object key : jsonObject.keySet()) {
                map.put((T1) key, (T2) jsonObject.get(key));
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static HashMap<Character, String> readJsonAsMap(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Type type = new TypeToken<HashMap<Character, String>>() {}.getType();
            return new Gson().fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static HashMap<String, Integer> readJsonAsMapStringInteger(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Type type = new TypeToken<HashMap<String, Integer>>() {}.getType();
            return new Gson().fromJson(reader, type);
        } catch (IOException e) {
           System.out.println("Erreur lors de la lecture du fichier : " + filePath );
            return null;
        }
    }

 public static HashMap<Character, Evaluateur.TouchInfo> loadDispositionFromJson(String dispositionPath) {
        HashMap<Character, Evaluateur.TouchInfo> dispositionMap = new HashMap<>();
        
        try (FileReader fr = new FileReader(dispositionPath);
             JsonReader jr = new JsonReader(fr)) {
            JsonObject root = JsonParser.parseReader(jr).getAsJsonObject();
            JsonObject dispoObject = root.getAsJsonObject("disposition");

            for (Map.Entry<String, JsonElement> entry : dispoObject.entrySet()) {
                String key = entry.getKey();
                JsonObject value = entry.getValue().getAsJsonObject();

                int rangee = value.get("rangee").getAsInt();
                int colonne = value.get("colonne").getAsInt();
                String doigt = value.get("doigt").getAsString();
                boolean home = value.get("home").getAsBoolean();


                char c = key.charAt(0);
                Evaluateur.TouchInfo info = new Evaluateur.TouchInfo(rangee, colonne, doigt, home);
                dispositionMap.put(c, info);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return dispositionMap;
    }

    public static void saveDispositionToJson(HashMap<Character, Evaluateur.TouchInfo> disposition, String dispositionPath) {
        JsonObject dispoObject = new JsonObject();
        for (Map.Entry<Character, Evaluateur.TouchInfo> entry : disposition.entrySet()) {
            char key = entry.getKey();
            Evaluateur.TouchInfo value = entry.getValue();

            JsonObject infoObject = new JsonObject();
            infoObject.addProperty("rangee", value.getRangee());
            infoObject.addProperty("colonne", value.getColonne());
            infoObject.addProperty("doigt", value.getDoigt());
            infoObject.addProperty("home", value.isHome());

            dispoObject.add(String.valueOf(key), infoObject);
        }

        JsonObject root = new JsonObject();
        root.add("disposition", dispoObject);

        try (FileWriter writer = new FileWriter(dispositionPath)) {
            writer.write(root.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
