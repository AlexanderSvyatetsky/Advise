package ua.dreambim.advise.dialog_fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import ua.dreambim.advise.R;
import ua.dreambim.advise.activities.UserProfileActivity;

/**
 * Created by MykhailoIvanov on 12/11/2016.
 */
public class DeleteArticleDialogFragment extends DialogFragment {

    private Activity activity;

    public CallbackInterface callbackInterface;

    public void setActivity(Activity activity)
    {
        this.activity = activity;
    }

    public Dialog onCreateDialog(Bundle bundle){
        Dialog dialog = super.onCreateDialog(bundle);

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle){
        View view = inflater.inflate(R.layout.dialogfragment_delete_article, container, false);

        view.findViewById(R.id.delete_button).setOnClickListener(new OnDeleteButtonClickListener());
        view.findViewById(R.id.cancel_button).setOnClickListener(new OnCancelButtonClickListener());

        return view;
    }

    public interface CallbackInterface{
        void callback();
    }


    private class OnDeleteButtonClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            DeleteArticleDialogFragment.this.dismiss();

            callbackInterface.callback();
        }
    }

    private class OnCancelButtonClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            DeleteArticleDialogFragment.this.dismiss();
        }
    }

}
