package a390l.team.rehapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static a390l.team.rehapp.R.id.SetCals;
import static a390l.team.rehapp.R.id.txtCalsgoal;

public class GoalPopUpActivity extends AppCompatActivity {
    private Button SetCals;
    private TextView txtCalsgoal;
    private int CalGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_pop_up);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int w = (int) (dm.widthPixels * 0.7);
        int h = (int) (dm.heightPixels * 0.7);

        getWindow().setLayout(w, h);

        CalGoal = (int) getIntent().getExtras().getInt("CaloriesGoal");
        txtCalsgoal = (TextView) findViewById(R.id.txtCalsgoal);
        txtCalsgoal.setText(String.valueOf(CalGoal));

        SetCals = (Button) findViewById(R.id.SetCals);
        SetCals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CalGoal >= 0) {
                    Intent i = new Intent();
                    int CGR =Integer.parseInt(txtCalsgoal.getEditableText().toString());
                    i.putExtra("CaloriesGoalReturn", CGR);
                    setResult(RESULT_OK, i);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "The Value Must be Positive!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
