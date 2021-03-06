package ir.android.taskroom.ui.fragment;

import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import ir.android.taskroom.R;
import ir.android.taskroom.utils.SettingUtil;
import ir.android.taskroom.data.db.entity.Reminders;
import ir.android.taskroom.databinding.ReminderFragmentBinding;
import ir.android.taskroom.ui.activity.reminder.AddEditReminderActivity;
import ir.android.taskroom.ui.adapters.ReminderAdapter;
import ir.android.taskroom.utils.Init;
import ir.android.taskroom.utils.enums.ShowCaseSharePref;
import ir.android.taskroom.viewmodels.ReminderViewModel;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReminderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReminderFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View inflaterView;
    private RecyclerView reminderRecyclerView;
    private SharedPreferences sharedPreferences;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private Integer mParam2;
    private ReminderViewModel reminderViewModel;
    private ReminderFragmentBinding reminderFragmentBinding;
    private ReminderAdapter reminderAdapter;
    private FloatingActionButton addReminderBtn;
    private Button firstAddReminderBtn;
    public static final int ADD_REMINDER_REQUEST = 1;
    public static final int EDIT_REMINDER_REQUEST = 2;
    private static final int SET_ALARM_PERMISION = 200;

    public ReminderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReminderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReminderFragment newInstance(String param1, Integer param2) {
        ReminderFragment fragment = new ReminderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        reminderFragmentBinding = DataBindingUtil.inflate(
                inflater, R.layout.reminder_fragment, container, false);
        View view = reminderFragmentBinding.getRoot();
        this.inflaterView = view;
        init();
        initRecyclerView();
        onClickListener();
        onTouchListener();
        return inflaterView;
    }

    private void onTouchListener() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Reminders selectedReminder = reminderAdapter.getReminderAt(viewHolder.getAdapterPosition());
                if (selectedReminder.getWork_id().contains(",")) {
                    for (String requestId : selectedReminder.getWork_id().split(",")) {
                        WorkManager.getInstance(getContext()).cancelWorkById(UUID.fromString(requestId));
                    }
                } else if(!selectedReminder.getWork_id().equals("0")) {
                    WorkManager.getInstance(getContext()).cancelWorkById(UUID.fromString(selectedReminder.getWork_id()));
                }
                if(selectedReminder.getReminders_id() >= 1000){
                    deleteCalendarEntry(selectedReminder.getReminders_id());
                } else {
                    reminderViewModel.delete(selectedReminder);
                }
                String successDeleteReminder = getString(R.string.successDeleteReminder);
                if(SettingUtil.getInstance(getContext()).isEnglishLanguage()){
                    successDeleteReminder = getString(R.string.successDeleteReminder);
                }
                Snackbar snackbar = Snackbar
                        .make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), successDeleteReminder, Snackbar.LENGTH_LONG);
                if(!SettingUtil.getInstance(getContext()).isEnglishLanguage()) {
                    ViewCompat.setLayoutDirection(snackbar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                }
                snackbar.show();
            }
        }).attachToRecyclerView(reminderRecyclerView);

    }

    private void onClickListener() {
        addReminderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddEditReminderActivity.class);
                startActivityForResult(intent, ADD_REMINDER_REQUEST);
            }
        });

        firstAddReminderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddEditReminderActivity.class);
                startActivityForResult(intent, ADD_REMINDER_REQUEST);
            }
        });

        reminderAdapter.setOnItemClickListener(new ReminderAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(Reminders reminders) {
                Intent intent = new Intent(getActivity(), AddEditReminderActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("clickedReminder", (Serializable) reminders);
                intent.putExtras(bundle);
                startActivityForResult(intent, EDIT_REMINDER_REQUEST);
            }
        });
    }

    private void initRecyclerView() {
        reminderAdapter = new ReminderAdapter(getActivity(), reminderViewModel);
        reminderViewModel.getAllReminders().observe(getViewLifecycleOwner(), new Observer<List<Reminders>>() {
            @Override
            public void onChanged(List<Reminders> reminders) {
                View remindersEmptyPage = inflaterView.findViewById(R.id.remindersEmptyPage);
                ConstraintLayout reminderLinear = inflaterView.findViewById(R.id.reminderLinear);
                if (reminders.size() == 0) {
                    String enterFirstRemidner = getString(R.string.enterFirstRemidner);
                    if(SettingUtil.getInstance(getContext()).isEnglishLanguage()){
                        enterFirstRemidner = getString(R.string.enterFirstRemidner);
                    }
                    Init.initShowCaseView(getContext(),firstAddReminderBtn, enterFirstRemidner, ShowCaseSharePref.FIRST_REMINDER_GUIDE.getValue(), null);
                    remindersEmptyPage.setVisibility(View.VISIBLE);
                    reminderLinear.setVisibility(View.GONE);
                    addReminderBtn.setVisibility(View.GONE);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(isAdded()) {

                                String editDeleteReminderGuide = getString(R.string.editDeleteReminderGuide);
                                if(SettingUtil.getInstance(getContext()).isEnglishLanguage()){
                                    editDeleteReminderGuide = getString(R.string.editDeleteReminderGuide);
                                }
                                Init.initShowCaseView(getContext(), reminderRecyclerView.getChildAt(0), editDeleteReminderGuide,
                                        ShowCaseSharePref.EDIT_DELETE_REMINDER_GUIDE.getValue(), null);
                            }
                        }
                    }, 1000);
                    remindersEmptyPage.setVisibility(View.GONE);
                    reminderLinear.setVisibility(View.VISIBLE);
                    addReminderBtn.setVisibility(View.VISIBLE);
                }
                reminderAdapter.submitList(reminders);
                reminderRecyclerView.setAdapter(reminderAdapter);
            }
        });

    }

    private void init() {
        reminderRecyclerView = this.inflaterView.findViewById(R.id.reminderRecyclerView);
        reminderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reminderViewModel = ViewModelProviders.of(this).get(ReminderViewModel.class);
        reminderFragmentBinding.setReminderViewModel(reminderViewModel);
        addReminderBtn = this.inflaterView.findViewById(R.id.addReminderBtn);
        firstAddReminderBtn = this.inflaterView.findViewById(R.id.firstAddReminderBtn);
        this.sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        if(SettingUtil.getInstance(getContext()).isEnglishLanguage()){
            firstAddReminderBtn.setText(getString(R.string.createNewReminder));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_REMINDER_REQUEST && resultCode == RESULT_OK) {
            String successInsertReminder = getString(R.string.successInsertReminder);
            if(SettingUtil.getInstance(getContext()).isEnglishLanguage()){
                successInsertReminder = getString(R.string.successInsertReminder);
            }
            Snackbar snackbar = Snackbar
                    .make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), successInsertReminder, Snackbar.LENGTH_LONG);
            if(!SettingUtil.getInstance(getContext()).isEnglishLanguage()) {
                ViewCompat.setLayoutDirection(snackbar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
            }
            snackbar.show();
            reminderAdapter.notifyDataSetChanged();
        } else if (requestCode == ADD_REMINDER_REQUEST && resultCode == RESULT_CANCELED) {
            Reminders reminders = new Reminders(0,"","",
                    0,"","",0,0,1,"", false);

            reminders.setReminders_id(sharedPreferences.getLong("tempReminderID", 0));
            reminderViewModel.delete(reminders);
        }
        if (requestCode == EDIT_REMINDER_REQUEST && resultCode == RESULT_OK) {
            String successEditReminder = getString(R.string.successEditReminder);
            if(SettingUtil.getInstance(getContext()).isEnglishLanguage()){
                successEditReminder = getString(R.string.successEditReminder);
            }
            Snackbar snackbar = Snackbar
                    .make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), successEditReminder, Snackbar.LENGTH_LONG);
            if(!SettingUtil.getInstance(getContext()).isEnglishLanguage()) {
                ViewCompat.setLayoutDirection(snackbar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
            }
            snackbar.show();
            reminderAdapter.notifyDataSetChanged();
        }
    }

    private int deleteCalendarEntry(long entryID) {
        int iNumRowsDeleted = 0;

        Uri eventUri = ContentUris
                .withAppendedId(CalendarContract.EventsEntity.CONTENT_URI, entryID - 1000);
        iNumRowsDeleted = getContext().getContentResolver().delete(eventUri, null, null);

        return iNumRowsDeleted;
    }


}