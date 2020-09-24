package ir.android.persiantask.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import ir.android.persiantask.R;

public class TasksRepeatDayBottomSheetFragment extends BottomSheetDialogFragment {
    private View inlfateView;
    private Button repeatDayBtn;
    private RepeatDayClickListener repeatDayClickListener;
    private AppCompatCheckBox saterday, sunday, monday, tuesday, wednesday, thursday, friday;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tasks_repeat_day, container,false);
        this.inlfateView = view;
        init();
        bundleFields();
        onClickListener();
        return view;
    }

    private void bundleFields() {
        Bundle bundle = getArguments();
        if(bundle != null) {
            String selectedDays = bundle.getString("selectedDays");
            if (selectedDays != null && !selectedDays.isEmpty()) {
                String[] selectedDaysList = selectedDays.split(",");
                ConstraintLayout dayList = inlfateView.findViewById(R.id.dayList);
                for (String day : selectedDaysList) {
                    for (int i = 0; i < dayList.getChildCount() - 1; i++) {
                        AppCompatCheckBox checkBox = (AppCompatCheckBox) dayList.getChildAt(i);
                        if (checkBox.getText().equals(day)) {
                            checkBox.setChecked(true);
                        }
                    }
                }
            }
        }
    }

    private void onClickListener() {
        repeatDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String repeatDays = "";
                repeatDays += saterday.isChecked() ? saterday.getText().toString() + ",": "";
                repeatDays += sunday.isChecked() ? sunday.getText().toString() + "," : "";
                repeatDays += monday.isChecked() ? monday.getText().toString() + "," : "";
                repeatDays += tuesday.isChecked() ? tuesday.getText().toString() + "," : "";
                repeatDays += wednesday.isChecked() ? wednesday.getText().toString() + "," : "";
                repeatDays += thursday.isChecked() ? thursday.getText().toString() + "," : "";
                repeatDays += friday.isChecked() ? friday.getText().toString() + ",": "";
                repeatDays = repeatDays.length() > 0 ? repeatDays.substring(0, repeatDays.length() - 1) : "";

                repeatDayClickListener.onClickRepeatDay(repeatDays);
                dismiss();
            }
        });
    }

    private void init() {
        repeatDayBtn = inlfateView.findViewById(R.id.repeatDayBtn);
        saterday = inlfateView.findViewById(R.id.saterday);
        sunday = inlfateView.findViewById(R.id.sunday);
        monday = inlfateView.findViewById(R.id.monday);
        tuesday = inlfateView.findViewById(R.id.tuesday);
        wednesday = inlfateView.findViewById(R.id.wednesday);
        thursday = inlfateView.findViewById(R.id.thursday);
        friday = inlfateView.findViewById(R.id.friday);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        repeatDayClickListener = (RepeatDayClickListener) context;
    }

    public interface RepeatDayClickListener {
        void onClickRepeatDay(String repeatDay);
    }
}
