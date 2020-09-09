package ir.android.persiantask.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import ir.android.persiantask.R;

public class TasksRepeatTypeBottomSheetFragment extends BottomSheetDialogFragment {
    private View inflatedView;
    private RadioGroup repeatedTypeVal;
    private RepeatTypeClickListener repeatTypeClickListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.tasks_repeat_type, container, false);
        this.inflatedView = inflatedView;
        init();
        onClickListener();
        return inflatedView;
    }

    private void onClickListener() {
        repeatedTypeVal.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = inflatedView.findViewById(checkedId);
                if(radioButton.getText().toString().equals(getResources().getString(R.string.selectDays))){
                    TasksRepeatDayBottomSheetFragment tasksRepeatDayBottomSheetFragment = new TasksRepeatDayBottomSheetFragment();
                    tasksRepeatDayBottomSheetFragment.show(getActivity().getSupportFragmentManager(), "Select_Day");
                } else if(radioButton.getText().toString().equals(getResources().getString(R.string.advance))){
                    TasksRepeatPeriodBottomSheetFragment tasksRepeatPeriodBottomSheetFragment = new TasksRepeatPeriodBottomSheetFragment();
                    tasksRepeatPeriodBottomSheetFragment.show(getActivity().getSupportFragmentManager(), "Select_Period");
                } else {
                    repeatTypeClickListener.onClickRepeatType(radioButton.getText().toString());
                }
                dismiss();
            }
        });
    }

    private void init() {
        repeatedTypeVal = this.inflatedView.findViewById(R.id.repeatedTypeVal);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        repeatTypeClickListener = (RepeatTypeClickListener) context;
    }

    public interface RepeatTypeClickListener {
        void onClickRepeatType(String repeatType);
    }
}
