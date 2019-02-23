package partlyapp.techpeg.com.partly.Activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import partlyapp.techpeg.com.partly.Constants.Constants;
import partlyapp.techpeg.com.partly.R;

public class NewUserActivity extends AppCompatActivity {

    @BindView(R.id.et_new_user_name)
    EditText et_name;
    @BindView(R.id.btn_new_user_start)
    Button btn_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.btn_new_user_start)
    public void getName() {
        String name = et_name.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter a name to continue", Toast.LENGTH_SHORT).show();
        } else {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(Constants.NAME_STRING_EXTRA, name);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {

    }
}