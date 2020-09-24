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

import java.util.ArrayList;

import ir.android.persiantask.R;

public class TasksRepeatTypeBottomSheetFragment extends BottomSheetDialogFragment {
    private View inflatedView;
    private RadioGroup repeatedTypeVal;
    private RepeatTypeClickListener repeatTypeClickListener;
    private String customDays, customPeriod;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.tasks_repeat_type, container, false);
        this.inflatedView = inflatedView;
        init();
        bundleFields();
        onClickListener();
        return inflatedView;
    }

    private void bundleFields() {
        Bundle bundle = getArguments();
        if(bundle != null && !bundle.getString("repeatDays").isEmpty()){
            int count =  repeatedTypeVal.getChildCount();
            ArrayList<RadioButton> listOfRadioButton = new ArrayList<RadioButton>();
            for(int i =0 ; i < count; i++){
                RadioButton radioButton = (RadioButton) repeatedTypeVal.getChildAt(i);
                if(radioButton.getText().toString().equals(bundle.getString("repeatDays"))){
                    radioButton.setChecked(true);
                } else if(bundle.getString("repeatDays").contains(",")){
                    ((RadioButton) repeatedTypeVal.getChildAt(4)).setChecked(true);
                    customDays = bundle.getString("repeatDays");
                    customDayClickEvent();
                } else{
                    ((RadioButton) repeatedTypeVal.getChildAt(5)).setChecked(true);
                    customPeriod = bundle.getString("repeatDays");
                    customPeriodClickEvent();
                }
            }
        }
    }

    private void customPeriodClickEvent() {
        repeatedTypeVal.getChildAt(5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TasksRepeatPeriodBottomSheetFragment tasksRepeatPeriodBottomSheetFragment = new TasksRepeatPeriodBottomSheetFragment();
                Bundle bundle = new Bundle();
                bundle.putString("selectedPeriod", customPeriod);
                tasksRepeatPeriodBottomSheetFragment.setArguments(bundle);
                tasksRepeatPeriodBottomSheetFragment.show(getActivity().getSupportFragmentManager(), "Select_Period");
                dismiss();
            }
        });
    }

    private void customDayClickEvent() {
        repeatedTypeVal.getChildAt(4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TasksRepeatDayBottomSheetFragment tasksRepeatDayBottomSheetFragment = new TasksRepeatDayBottomSheetFragment();
                Bundle bundle = new Bundle();
                bundle.putString("selectedDays", customDays);
                tasksRepeatDayBottomSheetFragment.setArguments(bundle);
                tasksRepeatDayBottomSheetFragment.show(getActivity().getSupportFragmentManager(), "Select_Day");
                dismiss();
            }
        });
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
