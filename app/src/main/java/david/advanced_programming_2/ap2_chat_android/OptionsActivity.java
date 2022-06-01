package david.advanced_programming_2.ap2_chat_android;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class OptionsActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private EditText serverEditText;
    private Button optionsConfirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        preferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        optionsConfirmBtn = findViewById(R.id.optionsConfirmBtn);

        serverEditText = findViewById(R.id.serverEditText);
        serverEditText.setText(preferences.getString("server", ""));

        optionsConfirmBtn.setOnClickListener(view -> handleConfirmOptions());

    }
    private void handleConfirmOptions() {
        String newServer = serverEditText.getText().toString();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("server", newServer);
        editor.commit();
        finish();
    }

}
