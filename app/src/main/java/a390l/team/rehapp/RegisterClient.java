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

public class RegisterClient extends AppCompatActivity implements View.OnClickListener {

    private Button btnRegister;
    private EditText editTextUsername, editTextAge, editTextMass, editTextEmail, editTextPassword;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_client);

        //gotoPertinentProfileActivity();

        // @IVAN ADD THE PART TO SEND THE MASS AND AGE TO THE DATABASE WHEN YOU REGISTER
        // YOU WILL ALSO HAVE TO MAKE A FUNCTION THAT UPDATES MASS, AGE CALORIC GOALS, AND CURRENT CALORIES BURNED
        // I PREFILLED SOME STUFF, BUT YOU MUST DO IT IN ORDER TO MAKE IT FUNCTION CORRECTLY, ONLY YOU KNOW HOW THIS WORKS.

        btnRegister = (Button) findViewById(R.id.btnClientRegister);
        editTextUsername = (EditText) findViewById(R.id.txtEditClientName);
        editTextAge = (EditText) findViewById(R.id.txtEditClientAge);
        editTextMass = (EditText) findViewById(R.id.txtEditClientMass);
        editTextEmail = (EditText) findViewById(R.id.txtEditClientEmail);
        editTextPassword = (EditText) findViewById(R.id.txtEditClientPass);

        progressDialog = new ProgressDialog(this);

        btnRegister.setOnClickListener(this);

    }

    private void registerUser() {

        final String clientName = editTextUsername.getText().toString().trim();
//        final String clientAge = editTextAge.getText().toString().trim();
//        final String clientWeight = editTextWeight.getText().toString().trim();
        final String clientEmail = editTextEmail.getText().toString().trim();
        final String clientPassword = editTextPassword.getText().toString().trim();

        progressDialog.setMessage("Registering client...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Const.URL_REGISTER_CLIENT,
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
                params.put("name", clientName);
//                params.put("clientage", username);
//                params.put("clientweight", username);
                params.put("email", clientEmail);
                params.put("password", clientPassword);
                //params.put("Distance",0)
                //params.put("Calories",0)
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
            case R.id.btnClientRegister:
                registerUser();
                break;

            default:
                break;
        }
    }

}
