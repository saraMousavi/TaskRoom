package ir.android.taskroom.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

import ir.android.taskroom.BuildConfig;
import ir.android.taskroom.R;
import ir.android.taskroom.utils.SettingUtil;
import ir.android.taskroom.ui.activity.MainActivity;
import ir.android.taskroom.ui.activity.setting.AboutAppActivity;
import ir.android.taskroom.ui.activity.category.CategoryActivity;
import ir.android.taskroom.ui.activity.theme.ThemeActivity;
import ir.android.taskroom.utils.enums.ShowCaseSharePref;

public class SettingFragment extends Fragment {

    private CollapsingToolbarLayout toolBarLayout;
    private View inflatedView;
    private SwitchCompat nightModeActive, languageActive;
    private LinearLayout projectCategory, themeFragment, shareApp, aboutApp, support, showCaseView, changeLanguageFragment;
    private SharedPreferences sharedPreferences;
    private TextView projectCategoryText, darkModeText, themeFragmentText, changeLanguageFragmentText,
            supportText, shareAppText, aboutAppText, showCaseViewText, appVersionText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View inflatedView = inflater.inflate(R.layout.setting_fragment, container, false);
        this.inflatedView = inflatedView;

        return inflatedView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        AppBarLayout mAppBarLayout = (AppBarLayout) this.inflatedView.findViewById(R.id.app_bar);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    toolBarLayout.setTitle(getString(R.string.Setting));
                    isShow = true;
                } else if (isShow) {
                    toolBarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
        clickEvents();
    }

    private void clickEvents() {
        projectCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent categoryIntent = new Intent(getActivity(), CategoryActivity.class);
                startActivity(categoryIntent);
            }
        });
        themeFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent themeActivity = new Intent(getActivity(), ThemeActivity.class);
                startActivity(themeActivity);
            }
        });
        shareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    String shareMessage = getString(R.string.app_name);
                    shareMessage += "\n" + getString(R.string.shareSubject) + "\n\n";
                    shareMessage += "http://cafebazaar.ir/app/" + BuildConfig.APPLICATION_ID + "/?l=fa\n\n";
                    sendIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, "choose one"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        aboutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutAppActivity.class);
                intent.putExtra("isFirstInvoke", 0);
                startActivity(intent);
            }
        });
        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:09944125972"));
                startActivity(intent);
            }
        });
        changeLanguageFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getContext(), LanguageActivity.class));
            }
        });
        showCaseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(ShowCaseSharePref.EDIT_DELETE_PROJECT_GUIDE.getValue());
                editor.remove(ShowCaseSharePref.EDIT_DELETE_TASK_GUIDE.getValue());
                editor.remove(ShowCaseSharePref.FIRST_PROJECT_GUIDE.getValue());
                editor.remove(ShowCaseSharePref.MORE_PROJECT_GUIDE.getValue());
                editor.remove(ShowCaseSharePref.FIRST_TASK_GUIDE.getValue());
                editor.remove(ShowCaseSharePref.FIRST_CALENDER_GUIDE.getValue());
                editor.remove(ShowCaseSharePref.FIRST_REMINDER_GUIDE.getValue());
                editor.remove(ShowCaseSharePref.EDIT_DELETE_REMINDER_GUIDE.getValue());
                editor.apply();
                Snackbar snackbar = Snackbar
                        .make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content),
                                getActivity().getString(R.string.successActiveUserGuide), Snackbar.LENGTH_SHORT);
                if(!SettingUtil.getInstance(getContext()).isEnglishLanguage()) {
                    ViewCompat.setLayoutDirection(snackbar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                }
                snackbar.show();
                return;
            }
        });
        nightModeActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SettingUtil.getInstance(getContext()).setDarkTheme(true);
                } else {
                    SettingUtil.getInstance(getContext()).setDarkTheme(false);
                }
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        });
        languageActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startActivity("en");
                } else {
                    startActivity("fa");
                }
            }
        });
    }

    private void init() {
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        toolBarLayout = (CollapsingToolbarLayout) this.inflatedView.findViewById(R.id.toolbar_layout);
        projectCategory = this.inflatedView.findViewById(R.id.projectCategory);
        themeFragment = this.inflatedView.findViewById(R.id.themeFragment);
        shareApp = this.inflatedView.findViewById(R.id.shareApp);
        aboutApp = this.inflatedView.findViewById(R.id.aboutApp);
        support = this.inflatedView.findViewById(R.id.support);
        showCaseView = this.inflatedView.findViewById(R.id.showCaseView);
        nightModeActive = this.inflatedView.findViewById(R.id.nightModeActive);
        languageActive = this.inflatedView.findViewById(R.id.languageActive);
        changeLanguageFragment = this.inflatedView.findViewById(R.id.changeLanguageFragment);

        if (SettingUtil.getInstance(getContext()).isDarkTheme()) {
            nightModeActive.setChecked(true);
        }
        if (SettingUtil.getInstance(getContext()).isEnglishLanguage()) {
            languageActive.setChecked(true);
        }
        final Toolbar toolbar = (Toolbar) this.inflatedView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolBarLayout = (CollapsingToolbarLayout) this.inflatedView.findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(" ");
    }


    private void startActivity(String language) {
        setLocale(language);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Intent intent;
        if (sharedPreferences.getInt("isFirstInstall", 0) == 1) {
            intent = new Intent(getContext(), MainActivity.class);
        } else {
            intent = new Intent(getContext(), AboutAppActivity.class);
            intent.putExtra("isFirstInvoke", 1);
            editor.putInt("isFirstInstall", 1);
        }
        SettingUtil.getInstance(getContext()).setEnglishLanguage(language.equals("en"));
        editor.apply();
        startActivity(intent);
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.setLocale(locale);
        getContext().getResources().updateConfiguration(configuration, getContext().getResources().getDisplayMetrics());
    }

}