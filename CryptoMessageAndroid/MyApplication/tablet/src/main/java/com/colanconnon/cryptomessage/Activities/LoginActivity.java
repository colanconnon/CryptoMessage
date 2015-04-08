package com.colanconnon.cryptomessage.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.colanconnon.cryptomessage.R;
import com.colanconnon.cryptomessage.Utils.DialogUtil;
import com.colanconnon.cryptomessage.Utils.JsonFetcher;

import org.json.JSONObject;


public class LoginActivity extends ActionBarActivity {
    private EditText loginUsernameEditTxt;
    private EditText loginPasswordEditTxt;
    private Button loginLoginBtn;
    private Button loginRegisterBtn;
    protected ProgressDialog progressDialog;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginUsernameEditTxt = (EditText) findViewById(R.id.loginUsernameEditTxt);
        loginPasswordEditTxt = (EditText) findViewById(R.id.loginPasswordEditTxt);
        loginLoginBtn = (Button) findViewById(R.id.loginLoginBtn);
        loginRegisterBtn = (Button) findViewById(R.id.loginRegisterBtn);
        context = LoginActivity.this;
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Processing login");
        loginLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidations()) {
                    //do a login here
                    new LoginNetworkTask().execute();
                }

            }
        });
        SharedPreferences prefs = context.getSharedPreferences("com.colanconnon.cryptomessage", Context.MODE_PRIVATE);
        String username = prefs.getString("username", null);
        if(username != null){
            Intent intent = new Intent(LoginActivity.this, TabletMessageActivity.class);

            LoginActivity.this.startActivity(intent);
            finish();
        }


        loginRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    public  boolean checkValidations(){
        if(!(loginUsernameEditTxt.getText().toString().length() > 0)){
            DialogUtil.showDialog(context, "Invalid username");
            return false;
        }
        if(!(loginPasswordEditTxt.getText().toString().length() > 0)){
            DialogUtil.showDialog(context, "Invalid password");
            return false;
        }
        return true;
    }
    private class LoginNetworkTask extends AsyncTask<Void,Void,JSONObject>{
        private static final String  URL = "http://cryptomessage.mobi/api/login/";
        @Override
        protected JSONObject doInBackground(Void... params) {
            try{
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username",loginUsernameEditTxt.getText().toString());
                jsonObject.put("password", loginPasswordEditTxt.getText().toString());

                JSONObject jsonObject1 = JsonFetcher.JSONPostToUrl(jsonObject, URL);
                return jsonObject1;
            }
            catch (Exception e){
                DialogUtil.showDialog(context, "Unknown error has occurred please try again");
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            progressDialog.dismiss();
            try{
                int success = jsonObject.getInt("success");
                System.out.println(success);
                if(success == 1){
                    SharedPreferences prefs = context.getSharedPreferences("com.colanconnon.cryptomessage", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("token",jsonObject.getString("token"));
                    editor.putString("username", loginUsernameEditTxt.getText().toString());
                    editor.putInt("userID", jsonObject.getInt("userID"));
                    editor.commit();

                    Toast.makeText(getApplicationContext(),"You are now logged in", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, TabletMessageActivity.class);

                    LoginActivity.this.startActivity(intent);
                }
                else if(success == 0){
                    DialogUtil.showDialog(context , "Incorrect login information provided!");
                }
                else{
                    DialogUtil.showDialog(context, jsonObject.toString());
                }
            }
            catch (Exception e){
                e.printStackTrace();
                DialogUtil.showDialog(context, "Unknown Error, please try again!");
            }
        }
    }

}
