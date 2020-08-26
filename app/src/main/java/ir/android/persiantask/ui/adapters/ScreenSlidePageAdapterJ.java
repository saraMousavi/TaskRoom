package ir.android.persiantask.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import ir.android.persiantask.ui.fragment.ScreenSlidePageFragmentJ;
import kotlin.jvm.internal.Intrinsics;

public class ScreenSlidePageAdapterJ extends FragmentStatePagerAdapter {
    public ScreenSlidePageAdapterJ(@NotNull ArrayList<ScreenSlidePageFragmentJ> fragmentList, @NotNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        Intrinsics.checkParameterIsNotNull(fragmentList, "fragmentList");
        Intrinsics.checkParameterIsNotNull(fm, "fm");
        this.fragmentList = fragmentList;
    }
    private final ArrayList<ScreenSlidePageFragmentJ> fragmentList;
    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position >= 0 && position < this.fragmentList.size()) {
            Fragment fragmentList = this.fragmentList.get(position);
            Intrinsics.checkExpressionValueIsNotNull(fragmentList, "fragmentList[position]");
            return fragmentList;
        } else {
            return (new ScreenSlidePageFragmentJ());
        }
    }

    @Override
    public int getCount() {
        return this.fragmentList.size();
    }
}
