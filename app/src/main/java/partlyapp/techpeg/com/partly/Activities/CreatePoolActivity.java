package partlyapp.techpeg.com.partly.Activities;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import partlyapp.techpeg.com.partly.Fragments.PoolNameFragment;
import partlyapp.techpeg.com.partly.R;

public class CreatePoolActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pool);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.new_pool_container, new PoolNameFragment());
        ft.commit();
    }


}
