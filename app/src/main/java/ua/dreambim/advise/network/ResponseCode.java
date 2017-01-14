package ua.dreambim.advise.network;

/**
 * Created by MykhailoIvanov on 12/18/2016.
 */
public class ResponseCode {

    public static boolean isSuccess(int status){
        return ((status >= 200) && (status < 300));
    }

}
