package ua.dreambim.advise.entities;

/**
 * Created by MykhailoIvanov on 11/27/2016.
 */
public class TheArticle {

    public static final String KEY_article_id = "_id";
    public static final String KEY_language = "language";
    public static final String KEY_title = "title";
    public static final String KEY_body = "body";
    public static final String KEY_authorNickname = "authorNickname";
    public static final String KEY_likesNumber = "likesNumber";
    public static final String KEY_commentsNumber = "commentsNumber";
    public static final String KEY_tags = "tags";
    public static final String KEY_date = "date";
    public static final String KEY_time = "time";
    public static final String KEY_liked = "liked";



    public String article_id;

    public int language; // 0 - english, 1 - russian

    public String title;
    public String body;

    public String authorNickname;

    public int likesNumber;
    public int commentsNumber;

    public String[] tags;

    public String date;
    public String time;

    public boolean liked; // for authorized user
    public static final int TAGS_FOR_AUTOCOMPLETE_LIMIT = 40;
    public static String[] tagsForAutocompleteText; // @TODO: update after language switch
}