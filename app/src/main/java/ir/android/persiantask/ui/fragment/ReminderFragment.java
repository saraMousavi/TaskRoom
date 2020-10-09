package ir.android.persiantask.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import net.vrgsoft.layoutmanager.RollingLayoutManager;

import java.io.Serializable;
import java.util.List;

import ir.android.persiantask.R;
import ir.android.persiantask.data.db.entity.Reminders;
import ir.android.persiantask.data.db.entity.Tasks;
import ir.android.persiantask.databinding.ReminderFragmentBinding;
import ir.android.persiantask.ui.activity.reminder.AddEditReminderActivity;
import ir.android.persiantask.ui.adapters.ReminderAdapter;
import ir.android.persiantask.utils.Init;
import ir.android.persiantask.utils.enums.ShowCaseSharePref;
import ir.android.persiantask.viewmodels.ReminderViewModel;

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
                reminderViewModel.delete(selectedReminder);

                Snackbar
                        .make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.successDeleteReminder), Snackbar.LENGTH_LONG)
                        .show();
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
                    Init.initShowCaseView(getContext(),firstAddReminderBtn, getString(R.string.enterFirstRemidner), ShowCaseSharePref.FIRST_REMINDER_GUIDE.getValue(), null);
                    remindersEmptyPage.setVisibility(View.VISIBLE);
                    reminderLinear.setVisibility(View.GONE);
                    addReminderBtn.setVisibility(View.GONE);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Init.initShowCaseView(getContext(),reminderRecyclerView.getChildAt(0), getString(R.string.editDeleteReminderGuide),
                                    ShowCaseSharePref.EDIT_DELETE_REMINDER_GUIDE.getValue(), null);
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
        reminderRecyclerView.setLayoutManager(new RollingLayoutManager(getContext()));
        reminderViewModel = ViewModelProviders.of(this).get(ReminderViewModel.class);
        reminderFragmentBinding.setReminderViewModel(reminderViewModel);
        addReminderBtn = this.inflaterView.findViewById(R.id.addReminderBtn);
        firstAddReminderBtn = this.inflaterView.findViewById(R.id.firstAddReminderBtn);
        this.sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_REMINDER_REQUEST && resultCode == RESULT_OK) {
            Snackbar
                    .make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.successInsertReminder), Snackbar.LENGTH_LONG)
                    .show();
            reminderAdapter.notifyDataSetChanged();
        } else if (requestCode == ADD_REMINDER_REQUEST && resultCode == RESULT_CANCELED) {
            Reminders reminders = new Reminders(0,"","",
                    0,"","",0,0,1,"", false);

            reminders.setReminders_id(sharedPreferences.getLong("tempReminderID", 0));
            reminderViewModel.delete(reminders);
        }
        if (requestCode == EDIT_REMINDER_REQUEST && resultCode == RESULT_OK) {
            Snackbar
                    .make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.successEditReminder), Snackbar.LENGTH_LONG)
                    .show();
            reminderAdapter.notifyDataSetChanged();
        }
    }

}