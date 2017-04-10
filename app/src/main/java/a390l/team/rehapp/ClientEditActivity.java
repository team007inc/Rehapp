package a390l.team.rehapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ClientEditActivity extends AppCompatActivity {

    protected Button mUpdate;
    protected TextView mCurrAge, mCurrMass;
    int Age,Mass, newAge, newMass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_edit);
        mUpdate = (Button)findViewById(R.id.mUpdate);
        mCurrAge = (TextView)findViewById(R.id.mCurrAge);
        mCurrMass = (TextView)findViewById(R.id.mCurrMass);


        Age = (int) getIntent().getExtras().getInt("Age");
        Mass = (int) getIntent().getExtras().getInt("Mass");

        mCurrAge.setText(String.valueOf(Age));
        mCurrMass.setText(String.valueOf(Mass));

        mUpdate.setOnClickListener(new View.OnClickListener() { //NonFunctional Saving features, to be done.
            @Override
            public void onClick(View view)
            {
                newAge =Integer.parseInt(mCurrAge.getEditableText().toString());
                newMass=Integer.parseInt(mCurrMass.getEditableText().toString());
                if(newAge>13 && newAge<100&& newMass> 50 && newMass<300) {
                    Intent i = new Intent();
                    i.putExtra("UpdatedMass", newMass);
                    i.putExtra("UpdatedAge",newAge );
                    i.putExtra("ReturnedStateID", 2); //Stamds for 2nd state return
                    setResult(RESULT_OK, i);
                    finish();
                }
                else {
                    if ((newMass < 50 || newMass > 300 ) && (newAge < 13 || newAge > 100))
                        Toast.makeText(getApplicationContext(), "Age and Mass Value are Incorrect", Toast.LENGTH_LONG).show();
                    else {
                        if (newAge < 13 || newAge > 100)
                            Toast.makeText(getApplicationContext(), "Age Value is Incorrect", Toast.LENGTH_LONG).show();
                        if (newMass < 50 || newMass > 300)
                            Toast.makeText(getApplicationContext(), "Mass Value is Incorrect", Toast.LENGTH_LONG).show();
                    }
                }
            }

        });



    }
}
