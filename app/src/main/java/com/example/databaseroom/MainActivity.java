package com.example.databaseroom;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {

    private AppDatabase appDatabase;
    private Button bt1;
    private TextView tw1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализируем экземпляр базы данных
        appDatabase = ((MyApp) getApplication()).getAppDatabase();

        // Получаем экземпляр базы данных с помощью метода getInstance
        appDatabase = AppDatabase.getInstance(getApplicationContext());

        // Создаем нового пользователя и асинхронно вставляем или обновляем его в базе данных
//        insertUser();

        // Настраиваем слушатель нажатия кнопки для получения пользователей из базы данных
        addListenerOnButton();
    }

    private void insertOrUpdateUser(User user) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Void> future = executor.submit(new InsertOrUpdateUserCallable(user));
        try {
            future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }

    private class InsertOrUpdateUserCallable implements Callable<Void> {
        private User user;

        public InsertOrUpdateUserCallable(User user) {
            this.user = user;
        }

        @Override
        public Void call() throws Exception {
            // Получаем DAO для работы с таблицей пользователей
            UserDao userDao = appDatabase.getUserDao();

            // Получаем пользователя с указанным ID из базы данных (если существует)
            User existingUser = userDao.getUserById(user.getId());

            if (existingUser != null) {
                // Если пользователь существует, обновляем его данные
                userDao.updateUser(user);
            } else {
                // Если пользователь не существует, вставляем его в базу данных
                userDao.insertUser(user);
            }
            return null;
        }
    }


    private void addListenerOnButton() {
        bt1 = findViewById(R.id.bt1);
        tw1 = findViewById(R.id.tw1);

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Выполняем вставку нового пользователя в базу данных в фоновом потоке
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        // Получаем DAO для работы с таблицей пользователей
                        UserDao userDao = appDatabase.getUserDao();

                        // Получаем список всех пользователей из базы данных
                        List<User> users = userDao.getAllUsers();

                        // Создаем нового пользователя и вставляем его в базу данных с уникальным id
                        User newUser = new User();
                        newUser.setId(users.size() + 1); // Устанавливаем уникальный id
                        newUser.setName("John Doe New");
                        newUser.setAge(25);
                        insertOrUpdateUser(newUser);

                        // Получаем обновленный список пользователей из базы данных
                        List<User> updatedUsers = userDao.getAllUsers();

                        // Показываем пользователей на UI (на основном потоке)
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showUsers(updatedUsers);
                            }
                        });
                    }
                });
                executor.shutdown();
            }
        });
    }

    private class GetUsersCallable implements Callable<List<User>> {
        @Override
        public List<User> call() throws Exception {
            // Получаем DAO для работы с таблицей пользователей
            UserDao userDao = appDatabase.getUserDao();

            // Получаем список всех пользователей из базы данных
            return userDao.getAllUsers();
        }
    }

    private void showUsers(List<User> users) {
        // Отображаем список пользователей в TextView
        StringBuilder stringBuilder = new StringBuilder();
        for (User user : users) {
            stringBuilder.append("ID: ").append(user.getId()).append(", Name: ").append(user.getName())
                    .append(", Age: ").append(user.getAge()).append("\n");
        }
        tw1.setText(stringBuilder.toString());
    }
}
