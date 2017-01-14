package ua.dreambim.advise.network;

/**
 * Created by MykhailoIvanov on 12/10/2016.
 */
public class URLParams {

    private String url;

    public URLParams(){
        url = new String();

    }

    public void add(String key, String value){
        if (url.length() == 0)
            url = "?" + key + "=" + value;
        else
            url += "&" + key + "=" + value;
    }

    public void add(String key, int value){
        if (url.length() == 0)
            url = "?" + key + "=" + value;
        else
            url += "&" + key + "=" + Integer.toString(value);
    }

    public String getURLParamsString()
    {
        return url;
    }

}
