package de.htwdd.htwdresden;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import de.htwdd.htwdresden.classes.Const;


public class HTWDDEventsSignUp extends Fragment {


    public static final int NICKNAME_LENGTH = 3;
    public static final int FIRSTNAME_LENGTH = 2;
    public static final int LASTNAME_LENGTH = 2;
    public static final int STG_LENGTH = 2;

    public HTWDDEventsSignUp() {
    }

    public static HTWDDEventsSignUp newInstance() {
        HTWDDEventsSignUp fragment = new HTWDDEventsSignUp();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public static void checkUserExist(final Context context, final String sNummer, final String password, final View view){
        //mPostCommentResponse.requestStarted();
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST,"http://www2.htw-dresden.de/~s72743/HTWDDEvents/testUser.php", new Response.Listener<String>() {
        //StringRequest sr = new StringRequest(Request.Method.POST,Const.internet.WEBSERVICE_URL_HISQIS + "getcourses", new Response.Listener<String>() {
        //StringRequest sr = new StringRequest(Request.Method.POST,"http://localhost:8888/testUser.php" + "getcourses", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //mPostCommentResponse.requestCompleted();
                Toast.makeText(context, "On response " + response, Toast.LENGTH_SHORT).show();
                Snackbar.make(view,"On response " + response,Snackbar.LENGTH_LONG);
                Log.e("SIGNUP", "On response " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "On Error Resp " + error, Toast.LENGTH_SHORT).show();
                Snackbar.make(view,"On Error Resp " + error,Snackbar.LENGTH_LONG);
                Log.e("SIGNUP", "On Error Resp " + error);
                //mPostCommentResponse.requestEndedWithError(error);
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("sNummer",Uri.encode(sNummer));
                Log.e("SIGNUP", "SNummer:" + sNummer);
                params.put("RZLogin", Uri.encode(password));
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_htwddevents_sign_up, container, false);

        //if(!Const.HTWEvents.isThereSNrAndPassw(getContext())) {startWizard(name,pass);}

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String url = Const.internet.WEBSERVICE_URL_HISQIS + "getcourses" +
                "?sNummer=s" + sharedPreferences.getString("sNummer", "") +
                "&RZLogin=" + Uri.encode(sharedPreferences.getString("RZLogin", ""));
        checkUserExist(getActivity(), "s72743", "Ecty5966", view);
        //if(!(pass.gehoertZu(user))) dasPasswortIstFalschMeldung();startWizard(name,pass);

        return view;
    }

    public interface OnFragmentInteractionListener {
        void onSignupFragmentInteraction(String string);
    }
}
