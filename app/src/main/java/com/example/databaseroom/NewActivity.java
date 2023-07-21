package com.example.databaseroom;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NewActivity extends AppCompatActivity {

    private TextView usersTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        usersTextView = findViewById(R.id.usersTextView);

        // Инициализируем экземпляр базы данных
        AppDatabase appDatabase = ((MyApp) getApplication()).getAppDatabase();

        // Получаем список пользователей из базы данных асинхронно
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<User>> future = executor.submit(new GetUsersCallable(appDatabase));
        try {
            List<User> users = future.get();
            showUsersInfo(users);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    private static class GetUsersCallable implements Callable<List<User>> {
        private final AppDatabase appDatabase;

        public GetUsersCallable(AppDatabase appDatabase) {
            this.appDatabase = appDatabase;
        }

        @Override
        public List<User> call() throws Exception {
            // Получаем DAO для работы с таблицей пользователей
            UserDao userDao = appDatabase.getUserDao();

            // Получаем список всех пользователей из базы данных
            return userDao.getAllUsers();
        }
    }

    private void showUsersInfo(List<User> users) {
        StringBuilder stringBuilder = new StringBuilder();
        for (User user : users) {
            stringBuilder.append("ID: ").append(user.getId()).append(", Name: ").append(user.getName())
                    .append(", Age: ").append(user.getAge()).append("\n");
        }
        usersTextView.setText(stringBuilder.toString());
    }
}
