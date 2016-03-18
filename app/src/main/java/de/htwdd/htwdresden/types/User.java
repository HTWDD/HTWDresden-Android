package de.htwdd.htwdresden.types;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
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

    public static final String POST_SNUMMER = "sNummer";
    public static final String POST_PASSWORD = "RZLogin";
    public static final String POST_NICKNAME = "nickname";
    public static final String POST_FISTNAME = "fisrtname";
    public static final String POST_LASTNAME = "lastname";
    public static final String POST_STUDIENGANG = "studiengang";

    public static final String POST_ACTION = "action";
    public static final String POST_ACTION_SIGNUP = "signup";

    private String sNummer;
    private String nickName;
    private String fisrtName;
    private String lastName;
    private String abschluss;
    private int    abschlNr;
    private String studiengang;
    private int    stgNr;
    private int    poVersion;

    public String getAbschluss() {
        return abschluss;
    }

    public void setAbschluss(String abschluss) {
        this.abschluss = abschluss;
    }

    public int getAbschlNr() {
        return abschlNr;
    }

    public void setAbschlNr(int abschlNr) {
        this.abschlNr = abschlNr;
    }

    public int getStgNr() {
        return stgNr;
    }

    public void setStgNr(int stgNr) {
        this.stgNr = stgNr;
    }

    public int getPoVersion() {
        return poVersion;
    }

    public void setPoVersion(int poVersion) {
        this.poVersion = poVersion;
    }

    public String getStudiengang() {
        return studiengang;
    }

    public void setStudiengang(String studiengang) {
        this.studiengang = studiengang;
    }

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
    static public String getUserNameSP(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return "s"+sharedPreferences.getString("sNummer", "");
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


        final ProgressDialog progressDialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("LOGIN");
        progressDialog.setMessage("Deine Daten werden überprüft...");
        progressDialog.setIndeterminate(true);
        progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.dismiss();
            }
        });
        progressDialog.show();

        StringRequest sr = new StringRequest(Request.Method.POST, "http://htwevents.metropoldesign.de/testUser.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //pd.dismiss();
                //mPostCommentResponse.requestCompleted();
                if (response.equalsIgnoreCase(RESPONSE_USER_SIGNED_UP)) {
                    Toast.makeText(context, "Du hast dich erfolgreich registriert.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    progressDialog.setMessage("Die SNummer entspricht dem Passwort nicht. Bitte versuchen Sie nochmal.");
                }
                Log.e("SIGNUP", "On response " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(context, "On Error Resp " + error, Toast.LENGTH_SHORT).show();
                Log.e("SIGNUP", "On Error Resp " + error);
                //mPostCommentResponse.requestEndedWithError(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(POST_ACTION, Uri.encode(POST_ACTION_SIGNUP));
                params.put(POST_SNUMMER, Uri.encode(sNummer));
                params.put(POST_PASSWORD, Uri.encode(password));
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
