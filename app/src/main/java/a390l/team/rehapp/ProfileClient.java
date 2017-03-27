package a390l.team.rehapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileClient extends AppCompatActivity {

    private TextView textViewClientname, textViewClientEmail;
    private Button mStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_client);


        if(!SharedPrefHelper.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        textViewClientname = (TextView) findViewById(R.id.txtViewClientName);
        textViewClientEmail = (TextView) findViewById(R.id.txtViewClientEmail);


        textViewClientEmail.setText(SharedPrefHelper.getInstance(this).getUserEmail());
        textViewClientname.setText(SharedPrefHelper.getInstance(this).getUsername());


        mStart = (Button) findViewById(R.id.mStart);
        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Intent = new Intent(ProfileClient.this, TrainingActivity.class);
                view.getContext().startActivity(Intent);}
        });



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
