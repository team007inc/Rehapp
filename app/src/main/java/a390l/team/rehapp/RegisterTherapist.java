package a390l.team.rehapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class RegisterTherapist extends AppCompatActivity implements View.OnClickListener {

    private Button btnRegister;
    private EditText editTextName, editTextEmail, editTextPassword;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_therapist);


        //gotoPertinentProfileActivity();


        btnRegister = (Button) findViewById(R.id.btnTherapistRegister);
        editTextName = (EditText) findViewById(R.id.txtTherapistName);
        editTextEmail = (EditText) findViewById(R.id.txtTherapistEmail);
        editTextPassword = (EditText) findViewById(R.id.txtTherapistPassword);

        progressDialog = new ProgressDialog(this);

        btnRegister.setOnClickListener(this);



    }

    private void registerUser() {
        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        progressDialog.setMessage("Registering Therapist...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Const.URL_REGISTER_THERAPIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            gotoPertinentProfileActivity();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };


        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);


    }




    public void gotoPertinentProfileActivity() {
        if (SharedPrefHelper.getInstance(this).isLoggedIn()) {
            if (SharedPrefHelper.getInstance(this).isTherapist()) {
                finish();
                startActivity(new Intent(this, ProfileTherapist.class));
            } else {
                finish();
                startActivity(new Intent(this, ProfileClient.class));
            }
        }else {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnTherapistRegister:
                registerUser();
                break;

            default:
                break;
        }
    }
    }