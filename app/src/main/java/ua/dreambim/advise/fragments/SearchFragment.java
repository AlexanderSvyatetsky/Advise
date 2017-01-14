package ua.dreambim.advise.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import ua.dreambim.advise.R;
import ua.dreambim.advise.activities.AdviseActivity;
import ua.dreambim.advise.custom_views.FlowLayout;
import ua.dreambim.advise.network.asynctasks.TopTagsGETAsyncTask;
import ua.dreambim.advise.onclicklisteners.OnTagClickListener;

/**
 * Created by MykhailoIvanov on 12/1/2016.
 */
public class SearchFragment extends Fragment {

    private AdviseActivity activity;

    public void setParameters(AdviseActivity activity)
    {
        this.activity = activity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle){
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        FlowLayout mandatoryTagsLayout = (FlowLayout) getView().findViewById(R.id.fragment_search_mandatory_tags_layout);
        String[] mandatoryTags = activity.getResources().getStringArray(R.array.strings_mandatory_tags_array);
        for (int i = 0; i < mandatoryTags.length; i++)
        {
            LinearLayout tagLinearLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.tag_mandatory, mandatoryTagsLayout, false);
            TextView tagTextView = (TextView) tagLinearLayout.findViewById(R.id.tag);
            tagTextView.setText(mandatoryTags[i]);

            tagLinearLayout.setOnClickListener(new OnTagClickListener(activity, mandatoryTags[i]));

            mandatoryTagsLayout.addView(tagLinearLayout);
        }

        (new TopTagsGETAsyncTask(activity, this)).execute(AdviseActivity.getLanguage(getActivity()), 100);
    }

    public void showTopTags(String[] topTags)
    {
        if (getView() == null)
            return;

        FlowLayout customTagsLayout = (FlowLayout) getView().findViewById(R.id.fragment_search_custom_tags_layout);

        for (int i = 0; i < topTags.length; i++)
        {
            LinearLayout tagLinearLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.tag_custom, customTagsLayout, false);
            TextView tagTextView = (TextView) tagLinearLayout.findViewById(R.id.tag);
            tagTextView.setText(topTags[i]);

            tagLinearLayout.setOnClickListener(new OnTagClickListener(activity, topTags[i]));

            customTagsLayout.addView(tagLinearLayout);
        }
    }
}
