package ir.android.persiantask.data.db.database;

import android.content.Context;

import androidx.annotation.WorkerThread;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ir.android.persiantask.data.db.dao.AttachmentsDao;
import ir.android.persiantask.data.db.dao.CategoryDao;
import ir.android.persiantask.data.db.dao.ColorsDao;
import ir.android.persiantask.data.db.dao.LabelDao;
import ir.android.persiantask.data.db.dao.ProjectsDao;
import ir.android.persiantask.data.db.dao.RemindersDao;
import ir.android.persiantask.data.db.dao.SubtasksDao;
import ir.android.persiantask.data.db.dao.TasksDao;
import ir.android.persiantask.data.db.entity.Attachments;
import ir.android.persiantask.data.db.entity.Category;
import ir.android.persiantask.data.db.entity.Colors;
import ir.android.persiantask.data.db.entity.Label;
import ir.android.persiantask.data.db.entity.Projects;
import ir.android.persiantask.data.db.entity.Reminders;
import ir.android.persiantask.data.db.entity.Subtasks;
import ir.android.persiantask.data.db.entity.Tasks;

@Database(entities = {Attachments.class, Category.class, Colors.class, Label.class,
        Projects.class, Reminders.class, Subtasks.class, Tasks.class},
        version = 1, exportSchema = false)
public abstract class PersianTaskDb extends RoomDatabase {
    private static PersianTaskDb sInstance;

    @WorkerThread
    public abstract AttachmentsDao attachmentsDao();

    @WorkerThread
    public abstract ProjectsDao projectsDao();

    @WorkerThread
    public abstract CategoryDao categoryDao();

    @WorkerThread
    public abstract ColorsDao colorsDao();

    @WorkerThread
    public abstract LabelDao labelDao();

    @WorkerThread
    public abstract RemindersDao remindersDao();

    @WorkerThread
    public abstract SubtasksDao subtasksDao();

    @WorkerThread
    public abstract TasksDao tasksDao();


    private static synchronized PersianTaskDb initialize(Context context) {
        sInstance = Room.databaseBuilder(context.getApplicationContext(),
                PersianTaskDb.class, "persiantask-database").
                fallbackToDestructiveMigration().build();
        return sInstance;
    }

    public static PersianTaskDb getInstance(Context context) {
        if (sInstance == null) {
            return initialize(context);
        } else {
            return sInstance;
        }
    }



    public static void destroyInstance() {
        sInstance = null;
    }
}
