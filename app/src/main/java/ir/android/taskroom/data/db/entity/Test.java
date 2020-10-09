package ir.android.taskroom.data.db.entity;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Test {
    private String id;
    private String title;

    public Test(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
