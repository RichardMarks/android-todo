package com.ramblingindie.todo.db;

import android.provider.BaseColumns;

/**
 * Created by rmarks on 9/9/17.
 */

public class TaskContract {
    public static final String DB_NAME = "com.ramblingindie.todo.tasksDB";
    public static final int DB_VERSION = 1;

    public class TaskEntry implements BaseColumns {
        public static final String TABLE = "tasks";
        public static final String TASK_NAME = "name";
    }

    public static String getCreateTableSQL() {
        return "CREATE TABLE " +
                TaskEntry.TABLE +
                " ( " +
                TaskEntry._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TaskEntry.TASK_NAME +
                " TEXT NOT NULL);";
    }

    public static String getDropTableSQL() {
        return "DROP TABLE IF EXISTS " + TaskEntry.TABLE;
    }
}
