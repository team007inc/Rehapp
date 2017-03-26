package a390l.team.rehapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText txtUserEmail, txtPassword;
    private Button btnLogin, btnRegister;
    private RadioButton rClient, rTherapist;
    private ProgressDialog progressDialog;
    /////////////////////////////
    private boolean isTherapist;
    private boolean returnValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        gotoPertinentProfileActivity();

        txtUserEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPass);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnReg);
        rClient = (RadioButton) findViewById(R.id.btnRadioC);
        rTherapist = (RadioButton) findViewById(R.id.btnRadioT);
        /////
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait for network access");
        /////
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        rClient.setOnClickListener(this);
        rTherapist.setOnClickListener(this);

    }

    private void userLogin(){
        String temp;
        if(isTherapist())
            temp = "true";
        else
            temp = "false";

        final String isTherapistString = temp.trim();
        final String useremail = txtUserEmail.getText().toString().trim();
        final String password = txtPassword.getText().toString().trim();



        progressDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Const.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(!obj.getBoolean("error")){
                                SharedPrefHelper.getInstance(getApplicationContext())
                                        .userLogin(
                                                obj.getInt("id"),
                                                obj.getString("name"),
                                                obj.getString("email"),
                                                isTherapist()
                                        );
                                gotoPertinentProfileActivity();
                            }else{
                                String temp = obj.getString("message").trim();
                                Toast.makeText(
                                        getApplicationContext(),
                                        temp,
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();

                        Toast.makeText(
                                getApplicationContext(),
                                error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", useremail);
                params.put("password", password);
                params.put("isTherapist", isTherapistString);
                return params;
            }

        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }


    public void gotoPertinentProfileActivity(){
        if(SharedPrefHelper.getInstance(this).isLoggedIn()){
            if(SharedPrefHelper.getInstance(this).isTherapist()){
                finish();
                startActivity(new Intent(this, ProfileTherapist.class));
            }else{
                finish();
                startActivity(new Intent(this, ProfileClient.class));
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnLogin:
                userLogin();
                break;
            case R.id.btnReg:
                if(isTherapist()) {
                    if(showAdminLogIn()) {
                        returnValue = false;
                        finish();
                        startActivity(new Intent(this, RegisterTherapist.class));
                    }
                }else{
                    finish();
                    startActivity(new Intent(this, RegisterClient.class));
                }
                break;
            case R.id.btnRadioC:
                    setTherapistMode(false);
                break;
            case R.id.btnRadioT:
                    setTherapistMode(true);
                break;

            default:
                break;
        }
    }

    public boolean showAdminLogIn(){



        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final TextView txtView = new TextView(this);
        final EditText pas = new EditText(this);
        txtView.setText("Enter Administrator password \n" + "to be able to register a new therapist");
        pas.setHint("Enter admin password");
        layout.addView(txtView);
        layout.addView(pas);
        alert.setView(layout);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
//                String value = pas.getText().toString();
//                Toast.makeText(getApplicationContext(), value,
//                        Toast.LENGTH_SHORT).show();

                if((pas.getText().toString().trim()).equals("admin")) {
                    returnValue = true;
                    dialog.cancel();
                    Toast.makeText(getApplicationContext(), "Administrator mode ON", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
                returnValue = false;
            }
        });
        alert.show();
        return returnValue;
    }



    public boolean isTherapist(){
        return isTherapist;
    }
    public void setTherapistMode(boolean mode){
        isTherapist = mode;
    }



}
