package ir.android.persiantask.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.vrgsoft.layoutmanager.RollingLayoutManager;

import java.util.ArrayList;
import java.util.List;

import ir.android.persiantask.R;
import ir.android.persiantask.data.db.entity.Reminders;
import ir.android.persiantask.ui.adapters.ReminderAdapter;

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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private Integer mParam2;

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
        // Inflate the layout for this fragment
        View inflaterView = inflater.inflate(R.layout.reminder_fragment, container, false);
        this.inflaterView = inflaterView;
        init();
        return inflaterView;
    }

    private void init() {
        reminderRecyclerView = this.inflaterView.findViewById(R.id.reminderRecyclerView);
        List<Reminders> remindersList = new ArrayList<>();
        Reminders reminders = new Reminders(1, "", 1, 1, getString(R.string.reminder), 1, "", 1, 1, 1);
        for(int i = 0 ; i < 10 ; i++){
            remindersList.add(reminders);
        }
        ReminderAdapter reminderAdapter = new ReminderAdapter(getActivity(), remindersList);
        reminderRecyclerView.setAdapter(reminderAdapter);
        reminderRecyclerView.setLayoutManager(new RollingLayoutManager(getContext()));
    }
}