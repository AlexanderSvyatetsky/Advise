package ua.dreambim.advise.onclicklisteners;

import android.app.Activity;
import android.support.v7.widget.SearchView;
import android.view.View;

import ua.dreambim.advise.R;
import ua.dreambim.advise.activities.AdviseActivity;

/**
 * Created by MykhailoIvanov on 12/3/2016.
 */
public class OnTagClickListener implements View.OnClickListener{

    private Activity activity;
    private String tag;

    public OnTagClickListener(Activity activity, String tag)
    {
        super();

        this.activity = activity;
        this.tag = tag;
    }

    @Override
    public void onClick(View view) {
        AdviseActivity.programmaticallySearchEnteredFlag = true;

        SearchView searchView = (SearchView) activity.findViewById(R.id.activity_advise_search);
        searchView.setIconified(false);
        searchView.setQuery(tag, true);
    }
}
