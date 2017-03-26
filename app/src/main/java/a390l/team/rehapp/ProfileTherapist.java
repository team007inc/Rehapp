package a390l.team.rehapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileTherapist extends AppCompatActivity {

    private TextView textViewTherapistName, textViewTherapistEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_therapist);


        if(!SharedPrefHelper.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        textViewTherapistName = (TextView) findViewById(R.id.txtViewTherapistName);
        textViewTherapistEmail = (TextView) findViewById(R.id.txtViewTherapistEmail);


        textViewTherapistEmail.setText(SharedPrefHelper.getInstance(this).getUserEmail());
        textViewTherapistName.setText(SharedPrefHelper.getInstance(this).getUsername());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menuLogout:
                SharedPrefHelper.getInstance(this).logOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.menuSettings:
                Toast.makeText(this, "Settings is empty so far", Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }
}