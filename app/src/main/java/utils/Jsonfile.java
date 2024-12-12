package utils;

import java.util.HashMap;
import java.io.FileWriter;
import java.io.IOException;
import org.json.simple.JSONObject;
import java.io.FileReader;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
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
            return new HashMap<>();
        }
    }
    
    public static HashMap<String, Integer> readJsonAsMapStringInteger(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Type type = new TypeToken<HashMap<String, Integer>>() {}.getType();
            return new Gson().fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }


}
