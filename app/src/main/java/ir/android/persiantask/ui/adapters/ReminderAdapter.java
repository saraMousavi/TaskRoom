package ir.android.persiantask.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ir.android.persiantask.R;
import ir.android.persiantask.data.db.entity.Reminders;

public class ReminderAdapter extends ListAdapter<Reminders, ReminderAdapter.ViewHolder> {
    private ReminderAdapter.OnItemClickListener listener;
    private FragmentActivity mFragmentActivity;

    private static final DiffUtil.ItemCallback<Reminders> DIFF_CALLBACK = new DiffUtil.ItemCallback<Reminders>() {
        @Override
        public boolean areItemsTheSame(@NonNull Reminders oldItem, @NonNull Reminders newItem) {
            return oldItem.getReminders_id() == newItem.getReminders_id();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Reminders oldItem, @NonNull Reminders newItem) {
            return oldItem.getReminders_title().equals(newItem.getReminders_title());
        }
    };


    public ReminderAdapter(FragmentActivity activity) {
        super(DIFF_CALLBACK);
        mFragmentActivity = activity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView remindersTitle;
        public SwitchCompat remindersActive;

        public ViewHolder(View itemView) {
            super(itemView);
            remindersTitle = itemView.findViewById(R.id.reminders_title);
            remindersActive = itemView.findViewById(R.id.reminders_active);
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
        LayoutInflater inflater = LayoutInflater.from(context);
        View taskView = inflater.inflate(R.layout.reminder_item_recyclerview, parent, false);
        ReminderAdapter.ViewHolder viewHolder = new ReminderAdapter.ViewHolder(taskView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ReminderAdapter.ViewHolder holder, int position) {
        Reminders reminder = getItem(position);
        holder.remindersTitle.setText(reminder.getReminders_title());
        holder.remindersActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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


}