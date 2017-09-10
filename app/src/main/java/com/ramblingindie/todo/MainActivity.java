package com.ramblingindie.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ramblingindie.todo.db.TaskContract;
import com.ramblingindie.todo.db.TaskDBHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TaskDBHelper dbHelper;

    private ListView todoListView;
    private ArrayAdapter<String> todoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new TaskDBHelper(this);

        todoListView = (ListView) findViewById(R.id.todoList);

        updateTodoListUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aboutMenuItem:
                showAboutDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showAboutDialog() {
        AlertDialog.Builder aboutBuilder = new AlertDialog.Builder(this);
        aboutBuilder.setTitle(R.string.about_title);
        aboutBuilder.setMessage(R.string.about_message);
        aboutBuilder.setPositiveButton(R.string.ok, null);
        aboutBuilder.setCancelable(false);

        AlertDialog aboutDialog = aboutBuilder.create();
        aboutDialog.show();
    }

    private void updateTodoListUI () {
        ArrayList<String> todos = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE, new String[]{
                TaskContract.TaskEntry._ID,
                TaskContract.TaskEntry.TASK_NAME
        }, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(TaskContract.TaskEntry.TASK_NAME);
            String taskName = cursor.getString(index);
            todos.add(taskName);
        }

        if (todoAdapter == null) {
            todoAdapter = new ArrayAdapter<>(this, R.layout.todo_item, R.id.task_name, todos);
            todoListView.setAdapter(todoAdapter);
        } else {
            todoAdapter.clear();
            todoAdapter.addAll(todos);
            todoAdapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();
    }

    public void onAddTaskButtonClick(View view) {
        EditText taskNameEdit = (EditText) findViewById(R.id.taskNameEdit);
        String taskName = taskNameEdit.getText().toString();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.TASK_NAME, taskName);
        db.insertWithOnConflict(TaskContract.TaskEntry.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        Context context = getApplicationContext();
        CharSequence text = "Added Task " + taskName;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        updateTodoListUI();
        taskNameEdit.setText("");
    }

    public void onTaskItemDoneButtonClick(View view) {
        View parent = (View) view.getParent();
        TextView taskItemTextView = (TextView) parent.findViewById(R.id.task_name);

        String taskName = taskItemTextView.getText().toString();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TaskContract.TaskEntry.TABLE, TaskContract.TaskEntry.TASK_NAME + " = ?", new String[]{taskName});
        db.close();

        Context context = getApplicationContext();
        CharSequence text = "Marked task as done";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        updateTodoListUI();
    }
}
