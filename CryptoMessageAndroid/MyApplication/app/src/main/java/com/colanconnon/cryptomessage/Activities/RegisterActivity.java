package com.colanconnon.cryptomessage.Activities;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.colanconnon.cryptomessage.R;
import com.colanconnon.cryptomessage.Utils.DialogUtil;
import com.colanconnon.cryptomessage.Utils.JsonFetcher;

import org.json.JSONObject;

public class RegisterActivity extends ActionBarActivity {
    private EditText registerUsernameEditTxt;
    private EditText registerPasswordEditTxt;
    private EditText registerConfirmPasswordEditTxt;
    private Button registerSubmit;
    private Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerUsernameEditTxt = (EditText) findViewById(R.id.registerUsernameEditTxt);
        registerPasswordEditTxt = (EditText) findViewById(R.id.registerPasswordEditTxt);
        registerConfirmPasswordEditTxt = (EditText) findViewById(R.id.registerConfirmPasswordEditTxt);
        registerSubmit = (Button) findViewById(R.id.registerSubmit);
        context = this;
        registerSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidations()){
                    new RegisterNetworkPost().execute();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private class RegisterNetworkPost extends AsyncTask<Void,Void,JSONObject>{
        private static final String URL = "http://cryptomessage.mobi/api/createuser/";
        @Override
        protected JSONObject doInBackground(Void... params) {

            try{
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username",registerUsernameEditTxt.getText().toString());
                jsonObject.put("password", registerPasswordEditTxt.getText().toString());

                JSONObject jsonObject1 = JsonFetcher.JSONPostToUrl(jsonObject, URL);
                return jsonObject1;
            }
            catch (Exception e){
                DialogUtil.showDialog(context, "Unknown Error, please try again!");
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject ) {
            super.onPostExecute(jsonObject);
            try{
                int statusCode = jsonObject.getInt("statusCode");
                System.out.println(statusCode);
                if(statusCode == 201){
                    DialogUtil.showDialog(context ,"You have successfully registered");
                }
                else if(statusCode == 400){
                    DialogUtil.showDialog(context , jsonObject.toString());
                }
                else{
                    DialogUtil.showDialog(context, jsonObject.toString());
                }
            }
            catch (Exception e){
                DialogUtil.showDialog(context, "Unknown Error, please try again!");
            }

        }
    }
    public boolean checkValidations(){
        if(!registerPasswordEditTxt.getText().toString().equals(registerConfirmPasswordEditTxt.getText().toString())){
            DialogUtil.showDialog(context, "Password's don't match");
            return false;
        }
        if(!(registerUsernameEditTxt.getText().toString().length() > 0)){
            DialogUtil.showDialog(context, "Not a valid username");
            return false;
        }
        if(!(registerPasswordEditTxt.getText().toString().length() >= 6)){
            DialogUtil.showDialog(context, "Password must be at least 6 characters");
            return false;
        }
        return true;
    }
}
