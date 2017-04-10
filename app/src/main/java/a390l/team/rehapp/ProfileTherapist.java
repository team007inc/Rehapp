package a390l.team.rehapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

//import static a390l.team.rehapp.R.layout.pop_up_activity;
import static a390l.team.rehapp.R.layout.rowlayout;

public class ProfileTherapist extends AppCompatActivity {

    private TextView textViewTherapistName, textViewProgressReport;
    private GridLayout mLinearlayout;
    private List<Client> myClients = new ArrayList<Client>();
    int CurrentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_therapist);

        if(!SharedPrefHelper.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        textViewTherapistName = (TextView) findViewById(R.id.txtViewTherapistName);
        textViewTherapistName.setText("Welcome " + SharedPrefHelper.getInstance(this).getUsername().toUpperCase());

        populateClientList();
        populateListView();
        registerClickCallback();

        Client[] Slackers = findSlackers();

        ProgressBar SlackersPB = (ProgressBar) findViewById(R.id.SlackersPB);
        SlackersPB.setMax((int)myClients.size());
        SlackersPB.setProgress((int) (myClients.size()-Slackers.length));
        SlackersPB.setScaleY(5f);
        Drawable drawable = SlackersPB.getProgressDrawable();
        drawable.setColorFilter(new LightingColorFilter(0xFF000000, 0xFFCC0000));

        if(Slackers.length==0) drawable.setColorFilter(new LightingColorFilter(0xFF000000, 0xFF00CC00));

        textViewProgressReport = (TextView)findViewById(R.id.ProgressReport);
        textViewProgressReport.setText("There Are " + Slackers.length + " Slackers out of "+ myClients.size() + ".");
    }


    private Client[] findSlackers() {
        List<Client> Slackers = new ArrayList<Client>();
        for(int i =0;i<myClients.size();i++){
            if(myClients.get(i).getCaloriesAssigned()>myClients.get(i).getCaloriesBurned())
            {
                Slackers.add(myClients.get(i));
            }
        }

        Client[] SlackerStr = Slackers.toArray(new Client[Slackers.size()]);
        return SlackerStr;
    }


    private void populateClientList() {
        myClients.add(new Client("Paul",22,90,0,500,1000));
        myClients.add(new Client("Ivan",24,75,1,300,100));
        myClients.add(new Client("Darnell",21,80,2,400,300));
        myClients.add(new Client("Hamza",22,70,3,100,50));
        myClients.add(new Client("Saddique",22,72,4,150,10));
        myClients.add(new Client("Saddique",22,72,4,150,10));
        myClients.add(new Client("Saddique",22,72,4,150,10));
        myClients.add(new Client("Saddique",22,72,4,150,10));
        myClients.add(new Client("Saddique",22,72,4,150,10));
        myClients.add(new Client("Saddique",22,72,4,150,10));
        myClients.add(new Client("Saddique",22,72,4,150,10));
        myClients.add(new Client("Saddique",22,72,4,150,10));
        myClients.add(new Client("Saddique",22,72,4,150,10));
        myClients.add(new Client("Saddique",22,72,4,150,10));
        myClients.add(new Client("Saddique",22,72,4,150,10));
        myClients.add(new Client("Saddique",22,72,4,150,10));
        myClients.add(new Client("Saddique",22,72,4,150,10));
        myClients.add(new Client("Saddique",22,72,4,150,10));
        myClients.add(new Client("Saddique",22,72,4,150,10));
        myClients.add(new Client("Saddique",22,72,4,150,10));
        myClients.add(new Client("Saddique",22,72,4,150,10));

        /*  USUALLY WE TAKE INPUTS IN THIS MANNER
        for(int i= 0;i<OnlineDatabaseListOfClients.LENGTH;i++)
        {
            myClients.add(OnlineDatabaseListOfClients(i));
        }
         */
    }

    private void populateListView() {
        ArrayAdapter<Client> adapter = new myListAdapter();
        ListView list = (ListView)findViewById(R.id.ClientListView);
        list.setAdapter(adapter);
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

    private class myListAdapter extends ArrayAdapter<Client> {
        public myListAdapter(){
            super(ProfileTherapist.this, rowlayout,myClients);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            double ProgPercent;

            View itemView = convertView;
            if(itemView == null) {
                itemView = getLayoutInflater().inflate(rowlayout, parent, false);
            }
            Client currentClient = myClients.get(position);

            TextView TVName = (TextView)itemView.findViewById(R.id.item_ClientListName);
            TVName.setText(currentClient.getName());

            //TextView TVName = (TextView)rowlayout.findViewById(R.id.item_ClientListName);
            //TVName.setText(currentClient.getName());

            TextView TVAge = (TextView)itemView.findViewById(R.id.item_ClientListAge);
            TVAge.setText(String.valueOf(currentClient.getAge() + " y"));

            TextView TVMass = (TextView)itemView.findViewById(R.id.item_ClientListMass);
            TVMass.setText(String.valueOf(currentClient.getMass() + "kg"));

            TextView TVASSIGNEDCALS = (TextView)itemView.findViewById(R.id.item_ClientListAssignedCalories);
            TVASSIGNEDCALS.setText(String.valueOf(currentClient.getCaloriesAssigned()));

            TextView TVBURNEDCALS = (TextView)itemView.findViewById(R.id.item_ClientListCurrentCalories);
            TVBURNEDCALS .setText(String.valueOf(currentClient.getCaloriesBurned()));

            ProgPercent = (double)currentClient.getCaloriesBurned()/currentClient.getCaloriesAssigned();

            ProgressBar ReportPB = (ProgressBar) itemView.findViewById(R.id.item_ClientProgress);
            if(ProgPercent<1.00) {
                ReportPB.setProgress((int)(ProgPercent*100));
                ReportPB.getIndeterminateDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_ATOP);
            }
            else    {
                ReportPB.setProgress(100);
                Drawable drawable = ReportPB.getProgressDrawable();
                drawable.setColorFilter(new LightingColorFilter(0xFF000000, 0xFF00C000));
            }
            return itemView;
        }

    }


    private void registerClickCallback() {
        ListView list = (ListView)findViewById(R.id.ClientListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {

                int Calories = myClients.get(position).getCaloriesAssigned();
                Intent Intent = new Intent(ProfileTherapist.this, GoalPopUpActivity.class);

                Intent.putExtra("CaloriesGoal", Calories);
                CurrentPosition = position;
                startActivityForResult(Intent,3);
                startActivityForResult(Intent,3);

            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int position = 0;
        int newCaloriesGoal = 0;
        if (requestCode == 3 && resultCode == RESULT_OK) // returnstate 1 means it is training activity
        {
            Client clickedClient = myClients.get(CurrentPosition);
            Intent Intent = new Intent();

            newCaloriesGoal = data.getIntExtra("CaloriesGoalReturn",clickedClient.getCaloriesAssigned());
            clickedClient.setCaloriesAssigned(newCaloriesGoal);
            clickedClient.setCaloriesAssigned(newCaloriesGoal);

            //**************Update this client on the Database*************

            emptyClientListView();
            populateClientList();
            populateListView();
        }

    }

    private void emptyClientListView() {
        ListView list = (ListView)findViewById(R.id.ClientListView);
        list.setAdapter(null);
    }


}