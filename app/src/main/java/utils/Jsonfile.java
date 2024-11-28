package utils;

import java.util.HashMap;
import java.io.FileWriter;
import java.io.IOException;
import org.json.simple.JSONObject;

public class Jsonfile<T1,T2> {
    
    public void create_json(HashMap<T1,T2> map, String path) {
        JSONObject obj = new JSONObject();
        for (T1 key : map.keySet()) {
            obj.put(key, map.get(key));
        }
        try (FileWriter file = new FileWriter(path)) {
            file.write(obj.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  HashMap<T1,T2> read_json(String path) {
        HashMap<T1,T2> map = new HashMap<T1,T2>();
        return map;
    }
    
}
