package ir.android.persiantask.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import ir.android.persiantask.R;
import kotlin.jvm.JvmStatic;


public class ScreenSlidePageFragmentJ extends Fragment {
    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_BG_COLOR = "arg_bg_color";
    private String title = "Default title.";
    private int bgColorResId = R.color.white;
    private View inflatedView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = this.getArguments();
        if (arguments != null) {
            this.title = arguments.getString(ARG_TITLE);
            this.bgColorResId = arguments.getInt(ARG_BG_COLOR);
        }

    }

    @Override
    @Nullable
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_screen_slide_page, container, false);
        this.inflatedView = inflatedView;
        return inflatedView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.inflatedView.setBackgroundColor(ContextCompat.getColor(Objects.requireNonNull(this.getContext()), this.bgColorResId));
        TextView screen_slide_title = this.inflatedView.findViewById(R.id.screen_slide_title);
        screen_slide_title.setText(this.title);
    }


    public void onDestroyView() {
        super.onDestroyView();
    }

    @JvmStatic
    @NotNull
    public static ScreenSlidePageFragmentJ newInstance(@NotNull String title, int bgColorId) {
        return Companion.newInstance(title, bgColorId);
    }

    public static final class Companion {
        @JvmStatic
        @NotNull
        public static ScreenSlidePageFragmentJ newInstance(@NotNull String title, int bgColorId) {
            ScreenSlidePageFragmentJ screenSlidePageFragmentJ = new ScreenSlidePageFragmentJ();
            Bundle bundle = new Bundle();
            bundle.putString(ARG_TITLE, title);
            bundle.putInt(ARG_BG_COLOR, bgColorId);
            screenSlidePageFragmentJ.setArguments(bundle);
            return screenSlidePageFragmentJ;
        }

        private Companion() {
        }

    }
}
