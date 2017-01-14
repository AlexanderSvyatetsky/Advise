package ua.dreambim.advise.dialog_fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import ua.dreambim.advise.R;
import ua.dreambim.advise.activities.CreateArticleActivity;

/**
 * Created by MykhailoIvanov on 12/21/2016.
 */
public class BackFromCreateArticleDialogFragment extends DialogFragment {

    private CreateArticleActivity activity;

    public void setActivity(CreateArticleActivity activity)
    {
        this.activity = activity;
    }

    public Dialog onCreateDialog(Bundle bundle){
        Dialog dialog = super.onCreateDialog(bundle);

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle){
        View view = inflater.inflate(R.layout.dialogfragment_back_from_create_article, container, false);

        view.findViewById(R.id.ok_button).setOnClickListener(new OnOkButtonClickListener());
        view.findViewById(R.id.cancel_button).setOnClickListener(new OnCancelButtonClickListener());

        return view;
    }

    private class OnOkButtonClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if (activity != null)
                activity.finish();
        }
    }

    private class OnCancelButtonClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            BackFromCreateArticleDialogFragment.this.dismiss();
        }
    }




}
