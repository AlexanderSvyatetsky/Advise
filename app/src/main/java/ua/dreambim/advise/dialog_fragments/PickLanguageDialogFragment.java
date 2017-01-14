package ua.dreambim.advise.dialog_fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.NavigationView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Locale;

import ua.dreambim.advise.R;
import ua.dreambim.advise.activities.AdviseActivity;

/**
 * Created by MykhailoIvanov on 12/11/2016.
 */
public class PickLanguageDialogFragment extends DialogFragment {

    private AdviseActivity activity;

    public void setActivity(AdviseActivity activity)
    {
        this.activity = activity;
    }

    public Dialog onCreateDialog(Bundle bundle){
        Dialog dialog = super.onCreateDialog(bundle);

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle){

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.dialogfragment_language, container);
        LinearLayout languagesLinearLayout = (LinearLayout) viewGroup.findViewById(R.id.languageslist_layout);
        languagesLinearLayout.setOrientation(LinearLayout.VERTICAL);

        String[] languages = activity.getResources().getStringArray(R.array.strings_languages);
        int currentLanguage = AdviseActivity.getLanguage(activity);
        for (int i = 0; i < languages.length; i++)
        {
            Button button = new Button(activity.getApplicationContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int margin = (int) getActivity().getResources().getDimension(R.dimen.activity_advise_language_button_margin_right_left);

            params.setMargins(margin, margin, margin, margin);
            button.setLayoutParams(params);
            // getDrawable requires API21 - we have API16
            if (currentLanguage != i)
                button.setBackground(activity.getResources().getDrawable(R.drawable.button_light));
            else
                button.setBackgroundResource(R.drawable.button_primary);
            button.setText(languages[i]);
            button.setTextColor(activity.getResources().getColor(R.color.colorTextAccentText));
            button.setOnClickListener(new LanguagePicked(i));

            languagesLinearLayout.addView(button);
        }


        return viewGroup;
    }

    private class LanguagePicked implements View.OnClickListener{

        private int language;

        public LanguagePicked(int language){
            this.language = language;
        }

        @Override
        public void onClick(View view) {

            activity.setLanguage(activity, language);

            //
            Resources resources = getResources();
            DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            android.content.res.Configuration configuration = resources.getConfiguration();
            configuration.locale = new Locale(AdviseActivity.getLanguageCode(AdviseActivity.getLanguage(activity)));
            resources.updateConfiguration(configuration, displayMetrics);
            //

            ((NavigationView) activity.findViewById(R.id.nav_view)).getMenu().clear();
            ((NavigationView) activity.findViewById(R.id.nav_view)).inflateMenu(R.menu.navigation_menu);

            PickLanguageDialogFragment.this.dismiss();
        }
    }

}
