package ir.android.taskroom.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
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
import androidx.work.impl.model.Preference;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.lang.reflect.Field;
import java.util.Objects;

import ir.android.taskroom.R;

public class TasksPriorityTypeBottomSheetFragment extends BottomSheetDialogFragment {
    private View inflateView;
    private NumberPicker priorityType;
    private Button priorityTypeBtn;
    private String[] priorityTypVal;
    private PriorityTypeClickListener priorityTypeClickListener;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.tasks_priority_type, container, false);
        this.inflateView = inflate;
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        onclickListener();
        return inflate;
    }

    private void onclickListener() {
        priorityTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isGone = priorityType.getValue() == 0;
                priorityTypeClickListener.onClickPriorityType(priorityTypVal[priorityType.getValue()], isGone);
                dismiss();
            }
        });
    }

    private void init() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        priorityType = inflateView.findViewById(R.id.priorityType);
        //@TODO get value from PriorityType enum
        priorityTypVal = new String[]{getString(R.string.nonePriority),
                getString(R.string.low),
                getString(R.string.medium),
                getString(R.string.high)};
        priorityType.setDisplayedValues(priorityTypVal);
        priorityType.setMinValue(0);
        priorityType.setMaxValue(3);
        priorityTypeBtn = inflateView.findViewById(R.id.priorityTypeBtn);
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(R.attr.textColor, typedValue, true);
        @ColorInt int textColor = typedValue.data;
        try {
            Field mSelectorWheelPaint = priorityType.getClass().getDeclaredField("mSelectorWheelPaint");
            Field mSelectionDivider = priorityType.getClass().getDeclaredField("mSelectionDivider");
            mSelectorWheelPaint.setAccessible(true);
            mSelectionDivider.setAccessible(true);
            ((Paint) Objects.requireNonNull(mSelectorWheelPaint.get(priorityType))).setColor(textColor);
            mSelectionDivider.set(priorityType, new ColorDrawable(textColor));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface PriorityTypeClickListener {
        void onClickPriorityType(String priorityType, boolean isGone);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        priorityTypeClickListener = (PriorityTypeClickListener) context;
    }
}
