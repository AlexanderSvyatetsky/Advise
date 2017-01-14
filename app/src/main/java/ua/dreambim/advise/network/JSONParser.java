package ua.dreambim.advise.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by MykhailoIvanov on 12/10/2016.
 */
public class JSONParser {

    public static JSONObject getJSONObject(InputStream inputStream)
    {
        if (inputStream == null)
            return null;

        String jsonString = new String();
        Scanner input = new Scanner(inputStream);
        while (input.hasNext())
            jsonString += input.next() + " ";

        try{
            return new JSONObject(jsonString);
        }catch(JSONException e){return null;}
    }

    public static JSONArray getJSONArray(InputStream inputStream)
    {
        if (inputStream == null)
            return null;

        String jsonString = new String();
        Scanner input = new Scanner(inputStream);
        while (input.hasNext())
            jsonString += input.next() + " ";

        try{
            return new JSONArray(jsonString);
        }catch(JSONException e){return null;}
    }

    private JSONObject jsonObject;

    public JSONParser(){
        jsonObject = new JSONObject();
    }

    public void addField(String key, String value){
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {}
    }

    public void addField(String key, int value){
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {}
    }

    public void addStringArray(String key, String[] value){
        try{
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < value.length; i++)
                jsonArray.put(value[i]);

            jsonObject.put(key, jsonArray);
        } catch (JSONException e) {}
    }

    public String getStringForResponseBody(){
        return jsonObject.toString();
    }

}
