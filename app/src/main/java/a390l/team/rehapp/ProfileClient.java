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

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.client_menu, menu);
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
            case R.id.editProfile:
                Toast.makeText(this, "Settings is empty so far", Toast.LENGTH_LONG).show();
                //startActivity(new Intent(this, ClientEditActivity.class));
                //Create an activity so that it saves Age and Weight.
                //This means we need to create a client class so that we can send extras from one activity to another.
                //The information is needed so we can shareto the databse dynamically, and the graph can be configured easier as well.
                break;
        }
        return true;
    }







}
