package ir.android.taskroom.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.lang.reflect.Field;
import java.util.Objects;

import ir.android.taskroom.R;
import ir.android.taskroom.utils.SettingUtil;

public class TasksRepeatPeriodBottomSheetFragment extends BottomSheetDialogFragment {
    private View inlfateView;
    private Button repeatPeriodBtn;
    private NumberPicker numberPeriod;
    private NumberPicker typePeriod;
    private RepeatPeriodClickListener repeatTypeClickListener;
    private String[] typePeriodVal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tasks_repeat_period, container, false);
        this.inlfateView = view;
        init();
        bundleFields();
        onClickListener();
        return view;
    }

    private void bundleFields() {
        Bundle bundle = getArguments();
        if (bundle != null && !bundle.getString("selectedPeriod").isEmpty()) {
            String[] selectedPeriod = bundle.getString("selectedPeriod").split(" ");
            numberPeriod.setValue(Integer.parseInt(selectedPeriod[1]));
            for (int i = 0; i < typePeriodVal.length; i++) {
                if (typePeriodVal[i].equals(selectedPeriod[2])) {
                    typePeriod.setValue(i);
                }
            }
        }
    }

    private void onClickListener() {
        repeatPeriodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                repeatTypeClickListener.onClickRepeatPeriod(getString(R.string.each) +
                        " " + numberPeriod.getValue() + " " +
                        typePeriodVal[typePeriod.getValue()]);
            }
        });
    }

    private void init() {
        repeatPeriodBtn = inlfateView.findViewById(R.id.repeatPeriodBtn);
        numberPeriod = inlfateView.findViewById(R.id.numberPeriod);
        numberPeriod.setMinValue(2);
        numberPeriod.setMaxValue(100);
        typePeriod = inlfateView.findViewById(R.id.typePeriod);
        //@TODO get value from RepeatType enum
        typePeriodVal = new String[]{getString(R.string.day), getString(R.string.week),
                getString(R.string.month), getString(R.string.year)};
        typePeriod.setDisplayedValues(typePeriodVal);
        typePeriod.setMinValue(0);
        typePeriod.setMaxValue(3);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(R.attr.textColor, typedValue, true);
        @ColorInt int textColor = typedValue.data;
        try {
            Field mSelectorWheelPaint = numberPeriod.getClass().getDeclaredField("mSelectorWheelPaint");
            Field mSelectionDivider = numberPeriod.getClass().getDeclaredField("mSelectionDivider");
            mSelectorWheelPaint.setAccessible(true);
            mSelectionDivider.setAccessible(true);
            ((Paint) Objects.requireNonNull(mSelectorWheelPaint.get(numberPeriod))).setColor(textColor);
            ((Paint) Objects.requireNonNull(mSelectorWheelPaint.get(typePeriod))).setColor(textColor);
            mSelectionDivider.set(numberPeriod, new ColorDrawable(textColor));
            mSelectionDivider.set(typePeriod, new ColorDrawable(textColor));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        repeatTypeClickListener = (RepeatPeriodClickListener) context;
    }

    public interface RepeatPeriodClickListener {
        void onClickRepeatPeriod(String repeatPeriod);
    }
}
