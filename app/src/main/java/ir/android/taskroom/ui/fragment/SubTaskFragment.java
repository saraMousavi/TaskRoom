package ir.android.taskroom.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ir.android.taskroom.R;
import ir.android.taskroom.data.db.entity.Subtasks;
import ir.android.taskroom.data.db.factory.SubTasksViewModelFactory;
import ir.android.taskroom.databinding.SubtaskFragmentBinding;
import ir.android.taskroom.ui.adapters.SubTasksAdapter;
import ir.android.taskroom.viewmodels.SubTasksViewModel;

public class SubTaskFragment extends Fragment {
    private SubtaskFragmentBinding subtaskFragmentBinding;
    private View inflatedView;
    private RecyclerView subtaskItemRecyclerView;
    private SubTasksViewModel subTasksViewModel;
    private SubTasksViewModelFactory factory;
    private LinearLayout subtaskListLine;
    private ImageView expandItem;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        subtaskFragmentBinding = DataBindingUtil.inflate(
                inflater, R.layout.subtask_fragment, container, false);
        View view = subtaskFragmentBinding.getRoot();
        this.inflatedView = view;
        init();
        onClickListener();
        subtaskFragmentBinding.setSubTaskViewModel(subTasksViewModel);
        return view;
    }

    private void onClickListener() {
        expandItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subtaskItemRecyclerView.setVisibility(subtaskItemRecyclerView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                expandItem.setImageResource(subtaskItemRecyclerView.getVisibility() == View.VISIBLE ? R.drawable.ic_arraw_up : R.drawable.ic_arraw_down);
            }
        });
    }

    private void init() {
        Bundle bundle = getArguments();
        Long selectedTaskID = null;
        if (bundle != null) {
            if (bundle.containsKey("taskID")) {
                selectedTaskID = bundle.getLong("taskID");
            }
        }
        subtaskItemRecyclerView = this.inflatedView.findViewById(R.id.subtaskItemRecyclerView);
        subtaskListLine = this.inflatedView.findViewById(R.id.subtaskListLine);
        expandItem = this.inflatedView.findViewById(R.id.expandItem);
        factory = new SubTasksViewModelFactory(getActivity().getApplication(), selectedTaskID);
        subTasksViewModel = ViewModelProviders.of(this, factory).get(SubTasksViewModel.class);
        SubTasksAdapter subTasksAdapter = new SubTasksAdapter(getActivity(), subTasksViewModel);
        subTasksViewModel.getAllTasksSubtasks().observe(getViewLifecycleOwner(), new Observer<List<Subtasks>>() {
            @Override
            public void onChanged(List<Subtasks> subtasks) {
                if (subtasks.size() > 0) {
                    int subtaskLineWidth = subtaskListLine.getWidth() / subtasks.size();
                    expandItem.setVisibility(View.VISIBLE);
                    for (Subtasks subtask : subtasks) {
                        View line = new View(getContext());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(subtaskLineWidth, 2);
                        params.setMargins(3, 0, 3, 0);
                        line.setLayoutParams(params);
                        if (subtask.getSubtasks_iscompleted() == 1) {
                            line.setBackground(getResources().getDrawable(R.color.colorAccent));
                        } else {
                            line.setBackground(getResources().getDrawable(R.color.grey_inactive));
                        }

                        subtaskListLine.addView(line);
                    }
                } else {
                    expandItem.setVisibility(View.GONE);
                }
                subtasks.add(null);
                subTasksAdapter.submitList(subtasks);
                subtaskItemRecyclerView.setAdapter(subTasksAdapter);
            }
        });
    }
}
