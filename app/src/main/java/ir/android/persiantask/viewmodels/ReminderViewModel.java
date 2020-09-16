package ir.android.persiantask.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import ir.android.persiantask.data.db.entity.Reminders;
import ir.android.persiantask.data.db.repository.RemindersRepository;

public class ReminderViewModel extends AndroidViewModel {
    private RemindersRepository remindersRepository;
    private LiveData<List<Reminders>> allReminders;

    public ReminderViewModel(@NonNull Application application) {
        super(application);
        remindersRepository = new RemindersRepository(application);
        allReminders = remindersRepository.getAllReminders();
    }

    public void insert(Reminders reminders) {
        remindersRepository.insert(reminders);
    }

    public void update(Reminders reminders){
        remindersRepository.update(reminders);
    }

    public void delete(Reminders reminders){
        remindersRepository.delete(reminders);
    }

    public LiveData<List<Reminders>> getAllReminders() {
        return allReminders;
    }
}
