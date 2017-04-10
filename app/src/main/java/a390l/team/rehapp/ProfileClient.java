package a390l.team.rehapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileClient extends AppCompatActivity {

    private TextView textViewClientname, textViewClientEmail, textViewTotalCalories,
            textViewTotalDistance, ageDisplay, massDisplay, mGoalFeedback;
    private Button mStart;
    private int CaloriesGoal=1;
    private int CaloriesOfTheDay =1;

    public Client thisClient = new Client("Paul", 22, 90,0); //**** @IVAN WHEN YOU WANT TO ADD YOUR PART, JUST MAKE SURE THAT WE HAVE THE CLIENT VALUES COMING IN AS SHOWN BELOW UNDER THE ONCREATE METHOD. ONCE DONE COMMENT THIS LINE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_client);

        if(!SharedPrefHelper.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        // @IVAN UNCOMMENT THIS ONE ONCE YOU DID ADD THE METHODS ON YOUR SIDE
        /*thisClient = new Client(SharedPrefHelper.getInstance(this).getUsername(),
                SharedPrefHelper.getInstance(this).getUserAge(),
                SharedPrefHelper.getInstance(this).getUserMass(), SharedPrefHelper.getInstance(this).getUserID());
        */

        textViewClientname = (TextView) findViewById(R.id.txtViewClientName);
        //textViewClientEmail = (TextView) findViewById(R.id.txtViewClientEmail); NOT NEEDED
        textViewTotalDistance = (TextView) findViewById(R.id.txtViewClientDistance);
        textViewTotalCalories = (TextView) findViewById(R.id.txtViewClientCalories);
        ageDisplay = (TextView) findViewById(R.id.ageDisp);
        massDisplay = (TextView) findViewById(R.id.massDisp);

        //textViewClientEmail.setText(SharedPrefHelper.getInstance(this).getUserEmail()); NOT NEEDED
        textViewClientname.setText( "Hello " + thisClient.getName().toUpperCase());
        massDisplay.setText( "Your Mass is " + thisClient.getMass()+  "kg!");
        ageDisplay.setText( "You are " + thisClient.getAge() + " Years Old!");

        //Receive the current Distance travelled and calories burned from the database. NOT NEED FOR DISTANCE FOR THE MOMENT
        //textViewTotalDistance.setText((int) SharedPrefHelper.getInstance(this).getUserDistance());

        //textViewTotalCalories.setText(SharedPrefHelper.getInstance(this).getUserCalories());************* @IVAN UPDATE THIS. I NEED THE TOTAL CALORIES THE CLIENT BURNED

        mGoalFeedback =(TextView)findViewById(R.id.mGoalFeedback);

        //START TRAINING BUTTON, SENDS YOU TO TRAINING ACTIVITY
        mStart = (Button) findViewById(R.id.mStart);
        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Intent = new Intent(ProfileClient.this, TrainingActivity.class);
                Intent.putExtra("Mass", thisClient.getMass());
                Intent.putExtra("Age", thisClient.getAge());
                startActivityForResult(Intent,1);}
        });

        // THIS UPDATES THE STATE OF THE PROGRESS OF THE CLIENT
        if(CaloriesGoal<=CaloriesOfTheDay){
            mGoalFeedback.setText("Goal for the day met!");
            mGoalFeedback.setTextColor(0xFF00C000);
        }
        else {
            mGoalFeedback.setText("Goal for the day not met!");
            mGoalFeedback.setTextColor(Color.RED);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.client_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        double distance=0;
        int calories =0;
        if(requestCode ==1 && resultCode == RESULT_OK ) // WHEN YOU RETURN FROM TRAINING ACTIVITY, YOU WILL GO THROUGHT THIS FUNCTION TO RETURN TO THIS PAGE
        {
            // RETREIVING THE DISTANCE
            distance = Double.parseDouble(textViewTotalDistance.getText().toString());
            distance = distance + data.getDoubleExtra("DistanceReturn",0);
            textViewTotalDistance.setText(String.valueOf( Math.round(distance*10)/10));

            //RETREIVING THE CALORIES.
            calories = Integer.parseInt(textViewTotalCalories.getText().toString());
            calories = calories + data.getIntExtra("CaloriesReturn",0);
            textViewTotalCalories.setText(String.valueOf(calories));
            CaloriesOfTheDay = calories;
            thisClient.SetCaloriesBurned(CaloriesOfTheDay); // UPDATING THE CLIENT PROFILE. EASIER TO UPDATE IF YOU MAKE A FUNCTION THAT TAKES CLIENT AND SENDS TO DATA BASE

            //******* @IVAN I NEED THE UPLOAD OF CALORIES BURNED THROUGHOUT THE ACTVITY. THIS IS A RETURN STATEMENT FROM THE TRAINING ACTIVITY
        }


        if(requestCode ==2 && resultCode == RESULT_OK) // WHEN YOU RETURN FROM THE EDIT ACTIVITY
        {
            thisClient.updateClientData(thisClient.ClientName,data.getIntExtra("UpdatedAge",thisClient.ClientAge),
                    data.getIntExtra("UpdatedMass",thisClient.ClientMass));
            String Message = "Updated Values of Age to " + String.valueOf(data.getIntExtra("UpdatedAge",thisClient.ClientAge))
                    + " and Mass to " + String.valueOf(data.getIntExtra("UpdatedMass",thisClient.ClientMass));
            Toast.makeText(getApplicationContext(), Message, Toast.LENGTH_LONG).show();

            //******* @IVAN I NEED THE UPLOAD OF AGE AND MASS TO THE CLOUD DATABASE. THIS IS A RETURN STATEMENT FROM THE EDIT ACTIVITY.

        }

        // THIS UPDATES THE STATE OF THE PROGRESS OF THE CLIENT
        if(CaloriesGoal<=CaloriesOfTheDay){
            mGoalFeedback.setText("Goal for the day met!");
            mGoalFeedback.setTextColor(0xFF00C000);
        }
        else {
            mGoalFeedback.setText("Goal for the day not met!");
            mGoalFeedback.setTextColor(Color.RED);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menuLogout:
                //LOGS YOU OUT OF YOUR PROFILE
                SharedPrefHelper.getInstance(this).logOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.editProfile:
                //SENDS YOU TO THE EDIT PROFILE ACTIVITY
                Intent Intent = new Intent(ProfileClient.this, ClientEditActivity.class);
                Intent.putExtra("Mass", thisClient.getMass());
                Intent.putExtra("Age", thisClient.getAge());
                startActivityForResult(Intent,2);
                break;
        }
        return true;
    }







}
