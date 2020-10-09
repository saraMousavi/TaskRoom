package ir.android.persiantask.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ir.android.persiantask.R;
import ir.android.persiantask.data.db.entity.Reminders;
import ir.android.persiantask.utils.Init;
import ir.android.persiantask.viewmodels.ReminderViewModel;

public class ReminderAdapter extends ListAdapter<Reminders, ReminderAdapter.ViewHolder> {
    private ReminderAdapter.OnItemClickListener listener;
    private FragmentActivity mFragmentActivity;
    private ReminderViewModel reminderViewModel;
    private SharedPreferences sharedPreferences;

    private static final DiffUtil.ItemCallback<Reminders> DIFF_CALLBACK = new DiffUtil.ItemCallback<Reminders>() {
        @Override
        public boolean areItemsTheSame(@NonNull Reminders oldItem, @NonNull Reminders newItem) {
            return oldItem.getReminders_id() == newItem.getReminders_id();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Reminders oldItem, @NonNull Reminders newItem) {
            return oldItem.getReminders_title().equals(newItem.getReminders_title()) &&
                    oldItem.getReminders_time().equals(newItem.getReminders_time()) &&
                    oldItem.getReminders_repeatedtype().equals(newItem.getReminders_repeatedtype()) &&
                    oldItem.getReminders_repeatedday().equals(newItem.getReminders_repeatedday());
        }
    };
    private Context context;


    public ReminderAdapter(FragmentActivity activity, ReminderViewModel reminderViewModel) {
        super(DIFF_CALLBACK);
        mFragmentActivity = activity;
        this.reminderViewModel = reminderViewModel;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView remindersTitle, remindertime, remindersRepeat;
        public SwitchCompat remindersActive;
        public View priorityView;
        public ImageView reminderComment, repeatedIcon, reminderAttachment;

        public ViewHolder(View itemView) {
            super(itemView);
            remindersTitle = itemView.findViewById(R.id.reminders_title);
            remindertime = itemView.findViewById(R.id.remindertime);
            remindersRepeat = itemView.findViewById(R.id.reminders_repeat);
            remindersActive = itemView.findViewById(R.id.reminders_active);
            reminderComment = itemView.findViewById(R.id.reminderComment);
            repeatedIcon = itemView.findViewById(R.id.repeatedIcon);
            reminderAttachment = itemView.findViewById(R.id.reminderAttachment);
            priorityView = itemView.findViewById(R.id.priorityView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.OnItemClick(getItem(position));
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ReminderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        this.context = context;
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        setMasterTheme(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View taskView = inflater.inflate(R.layout.reminder_item_recyclerview, parent, false);
        ReminderAdapter.ViewHolder viewHolder = new ReminderAdapter.ViewHolder(taskView);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull final ReminderAdapter.ViewHolder holder, int position) {
        Reminders reminder = getItem(position);
        holder.remindersTitle.setText(reminder.getReminders_title());
        holder.remindersActive.setChecked(reminder.getReminders_active() == 1 ? true : false);
        holder.remindertime.setText(reminder.getReminders_time());
        if (reminder.getReminders_comment().isEmpty()) {
            holder.reminderComment.setVisibility(View.GONE);
        } else {
            holder.reminderComment.setVisibility(View.VISIBLE);
        }
        if (reminder.getReminders_repeatedday().isEmpty()) {
            holder.repeatedIcon.setVisibility(View.GONE);
        } else {
            holder.repeatedIcon.setVisibility(View.VISIBLE);
            holder.remindersRepeat.setText("," +reminder.getReminders_repeatedday());
        }
        if(reminder.getHas_attach()){
            holder.reminderAttachment.setVisibility(View.VISIBLE);
        } else {
            holder.reminderAttachment.setVisibility(View.GONE);
        }

        if (sharedPreferences.getBoolean("NIGHT_MODE", false)) {
            holder.remindersTitle.setTextColor(context.getResources().getColor(R.color.white));
            holder.remindersRepeat.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            ArrayList<Map<View, Boolean>> viewList = new ArrayList<>();
            Map<View, Boolean> mapView =  new HashMap<>();
            mapView.put(holder.remindersTitle, false);
            viewList.add(mapView);
            mapView =  new HashMap<>();
            mapView.put(holder.remindersRepeat, false);
            viewList.add(mapView);
            Init.setViewBackgroundDependOnTheme(viewList, context, false);
        }
        if(reminder.getReminders_priority() == 1){
            holder.priorityView.setBackground(mFragmentActivity.getResources().getDrawable(R.drawable.yellow_priority_corner_shape));
        } else if(reminder.getReminders_priority() == 2){
            holder.priorityView.setBackground(mFragmentActivity.getResources().getDrawable(R.drawable.orange_priority_corner_shape));
        } else if(reminder.getReminders_priority() == 3){
            holder.priorityView.setBackground(mFragmentActivity.getResources().getDrawable(R.drawable.green_priority_corner_shape));
        }
        holder.remindersActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                reminder.setReminders_active(isChecked ? 1 : 0);
                reminder.setReminders_id(reminder.getReminders_id());
                reminderViewModel.update(reminder);
            }
        });
    }

    public Reminders getReminderAt(int position) {
        return getItem(position);
    }

    public interface OnItemClickListener {
        void OnItemClick(Reminders reminders);
    }

    public void setOnItemClickListener(ReminderAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }


    public void setMasterTheme(Context context) {
        if (sharedPreferences.getBoolean("NIGHT_MODE", false)) {
            context.setTheme(R.style.FeedActivityThemeDark);
            return;
        }
        switch (getFlag(context)) {
            case 2:
                context.setTheme(R.style.AppTheme2);
                break;
            case 3:
                context.setTheme(R.style.AppTheme3);
                break;
            case 4:
                context.setTheme(R.style.AppTheme4);
                break;
            case 5:
                context.setTheme(R.style.AppTheme5);
                break;
            case 6:
                context.setTheme(R.style.AppTheme6);
                break;
            default:
                context.setTheme(R.style.AppTheme);
                break;
        }
    }


    public Integer getFlag(Context context) {
        SharedPreferences sharedpreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedpreferences.getInt("theme", 1);
    }
}