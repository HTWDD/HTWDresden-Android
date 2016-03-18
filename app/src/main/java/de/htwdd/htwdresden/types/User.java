package de.htwdd.htwdresden.types;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class User {

    public static final String RESPONSE_USER_SIGNED_UP = "signedup";

    String sNummer;
    String nickName;
    String fisrtName;
    String lastName;
    String studiengang;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFisrtName() {
        return fisrtName;
    }

    public void setFisrtName(String fisrtName) {
        this.fisrtName = fisrtName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getsNummer() {
        return sNummer;
    }

    public void setsNummer(String sNummer) {
        this.sNummer = sNummer;
    }

    static public boolean isUserSignedUp(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("SignedUp", false);
    }

    static public boolean isThereSNrAndPassw(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return !(sharedPreferences.getString("sNummer", "").length() < 5 || sharedPreferences.getString("RZLogin", "").length() < 3);
    }

    public static void checkUserExistAPI(final Context context, final View view, PostRequestResponseListener responseListener) {
        responseListener.requestStarted();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String sNummer = sharedPreferences.getString("sNummer", "");
        final String password = sharedPreferences.getString("RZLogin", "");


        final ProgressDialog pd = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
        pd.setTitle("LOGIN");
        pd.setMessage("Deine Daten werden überprüft...");
        pd.setIndeterminate(true);
        pd.setButton(ProgressDialog.BUTTON_NEGATIVE, "Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pd.dismiss();
            }
        });
        pd.show();

        StringRequest sr = new StringRequest(Request.Method.POST, "http://htwevents.metropoldesign.de/testUser.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //pd.dismiss();
                //mPostCommentResponse.requestCompleted();
                if (response.equalsIgnoreCase(RESPONSE_USER_SIGNED_UP)) {
                    Toast.makeText(context, "Du hast dich erfolgreich registriert.", Toast.LENGTH_SHORT).show();
                }
                Log.e("SIGNUP", "On response " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(context, "On Error Resp " + error, Toast.LENGTH_SHORT).show();
                Snackbar.make(view, "On Error Resp " + error, Snackbar.LENGTH_LONG);
                Log.e("SIGNUP", "On Error Resp " + error);
                //mPostCommentResponse.requestEndedWithError(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("sNummer", Uri.encode(sNummer));
                Log.e("SIGNUP", "SNummer:" + sNummer);
                params.put("RZLogin", Uri.encode(password));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(sr);
    }

    public interface PostRequestResponseListener {
        void requestStarted();
        //public void requestCompleted();
        //public void requestEndedWithError(VolleyError error);
    }

}
