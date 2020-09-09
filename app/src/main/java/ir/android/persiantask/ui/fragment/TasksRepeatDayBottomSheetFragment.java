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

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

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
        onClickListener();
        return view;
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
