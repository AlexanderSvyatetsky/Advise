package ua.dreambim.advise.entities;

/**
 * Created by MykhailoIvanov on 11/27/2016.
 */
public class TheComment {

    public static final String KEY_comment_id = "_id";
    public static final String KEY_comment_body = "body";
    public static final String KEY_comment_authorNickname = "authorNickname";

    public String comment_id;
    public String article_id; // placement

    public String body;
    public String authorNickname;

}
