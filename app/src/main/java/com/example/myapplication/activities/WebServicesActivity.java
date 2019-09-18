package com.example.myapplication.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class WebServicesActivity extends AppCompatActivity implements View.OnClickListener {
    EditText edtName, edtEmail,txtresponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_services);

        init();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    private void init(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        edtName = findViewById(R.id.edtName);
        edtEmail =findViewById(R.id.edtEmail);
        findViewById(R.id.btnGet).setOnClickListener(this);
        findViewById(R.id.btnPost).setOnClickListener(this);
        findViewById(R.id.btnDelete).setOnClickListener(this);
        findViewById(R.id.btnPut).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnGet:
                getResponse("https://reqres.in/api/unknown/2");
                break;
            case R.id.btnPost:
                postService("https://reqres.in/api/users");
                break;
            case R.id.btnPut:
                putResponse("https://reqres.in/api/users/2");
            case R.id.btnDelete:
                deleteResponse("https://reqres.in/api/users/2");
        }
    }

    private void putResponse(String s) {
        final String name = edtName.getText().toString().trim();
        final String job = edtEmail.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, s, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Response :" + response, Toast.LENGTH_LONG).show();//display the response on screen
                parsePutResponse(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(WebServicesActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("job", job);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void parsePutResponse(String response) {
        try {
            JSONObject responseJsonObject = new JSONObject(response);
            String updateAt = responseJsonObject.getString("updateAt");
            String name = responseJsonObject.getString("name");
            int id = responseJsonObject.getInt("id");
            txtresponse.setText(name);
            txtresponse.setText(id);
            txtresponse.setText(updateAt);
        } catch (Exception e) {
            Log.e("parsePUTResponse", e.toString());
        }
    }

    private void deleteResponse(String s) {
        RequestQueue mRequestQueue;
        StringRequest mStringRequest;
        mRequestQueue = Volley.newRequestQueue(this);
        mStringRequest = new StringRequest(Request.Method.DELETE, s, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("onResponse: ", response);
                Toast.makeText(getApplicationContext(), "Response :" + response, Toast.LENGTH_LONG).show();//display the response on screen

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("Error :", error.toString());
            }
        });
        mRequestQueue.add(mStringRequest);
    }

    private void postService(String s) {
        final String name = edtName.getText().toString().trim();
        final String job = edtEmail.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, s,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(WebServicesActivity.this, response, Toast.LENGTH_LONG).show();
                        parsePostResponse(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(WebServicesActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("job", job);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void parsePostResponse(String response) {
        try {
            JSONObject responseJSONObject = new JSONObject(response);
            int id = responseJSONObject.getInt("id");
            String name = responseJSONObject.getString("name");
            String job = responseJSONObject.getString("job");
            String createdAt = responseJSONObject.getString("createdAt");
            txtresponse.setText(createdAt);
            txtresponse.setText(job);
            txtresponse.setText(name);
            txtresponse.setText(id);
        } catch (Exception e) {
            Log.e("parsePOSTResponse", e.toString());
        }
    }

    private void getResponse(String s) {
        RequestQueue mRequestQueue;
        StringRequest mStringRequest;
        mRequestQueue = Volley.newRequestQueue(this);
        mStringRequest = new StringRequest(Request.Method.GET, s, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                parseGetResponse(response);
                Log.e("onResponse: ", response);
                Toast.makeText(getApplicationContext(), "Response :" + response, Toast.LENGTH_LONG).show();//display the response on screen

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("Error :", error.toString());
                Toast.makeText(WebServicesActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        mRequestQueue.add(mStringRequest);
    }

    private void parseGetResponse(String response) {
        try {
            JSONObject responseJSONObject = new JSONObject(response);
            int page = responseJSONObject.getInt("page");
            String perpage = responseJSONObject.getString("per_page");
            String total = responseJSONObject.getString("total");
            String totalPage = responseJSONObject.getString("total_pages");
            JSONArray dataJSONArray = responseJSONObject.getJSONArray("data");
            for (int i = 0; i < dataJSONArray.length(); i++) {
                JSONObject dataJSONObject = dataJSONArray.getJSONObject(i);
                int id = dataJSONObject.getInt("id");
                String email = dataJSONObject.getString("email");
                String firstname = dataJSONObject.getString("first_name");
                String lastname = dataJSONObject.getString("last_name");
                String avatar = dataJSONObject.getString("avatar");
            }

        } catch (Exception e) {
            Log.e("parseGetResponse", e + "");
        }

    }
}
