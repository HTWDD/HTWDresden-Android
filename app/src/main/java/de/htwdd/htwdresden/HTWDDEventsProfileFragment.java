package de.htwdd.htwdresden;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.VolleyDownloader;


public class HTWDDEventsProfileFragment extends Fragment {


    private OnFragmentInteractionListener mListener;

    public HTWDDEventsProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_htwddevents_profile, container, false);

        //Hole das JSON-Objekt aus Const.SEMESTERPLAN_URL_JSON und initialisiere das Objekt von SemesterPlan
        final Response.Listener<JSONArray> jsonArrayListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject semesterPlanJSON = response.getJSONObject(i);
                        int sNummer = semesterPlanJSON.getInt("ID");
                        String vorName = semesterPlanJSON.getString("NAME");
                        String nachName = semesterPlanJSON.getString("VORNMAE");
                        String nickname = semesterPlanJSON.getString("NICKNAMEh");
                        Snackbar.make(view, sNummer + " "+vorName+" "+ nachName + " " + nickname, Snackbar.LENGTH_LONG).show();
                        //setSemesterPlanView(view);
                        final TextView userName = (TextView) view.findViewById(R.id.events_profile_user_name);
                        final TextView userId = (TextView) view.findViewById(R.id.events_profile_user_id);
                        final TextView nickName = (TextView) view.findViewById(R.id.textView2);
                        if(userId == null || userName == null || nickName == null ) return;
                        userName.setText(vorName + " " + nachName);
                        userId.setText(sNummer+"");
                        nickName.setText(nickname);
                    } catch (JSONException e) {
                        Log.e("JSON SEMESTERPLAN", "JSON IS BROKEN");
                        e.printStackTrace();
                    }
                }
            }
        };

        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Bestimme Fehlermeldung
                int responseCode = VolleyDownloader.getResponseCode(error);
                // Fehlermeldung anzeigen
                String message;
                switch (responseCode) {
                    case Const.internet.HTTP_TIMEOUT:
                        message = getString(R.string.info_internet_timeout);
                        break;
                    case Const.internet.HTTP_NO_CONNECTION:
                    case Const.internet.HTTP_NOT_FOUND:
                        message = getString(R.string.info_internet_no_connection);
                        break;
                    case Const.internet.HTTP_NETWORK_ERROR:
                    default:
                        message = getString(R.string.info_internet_error);
                }
                Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
                // Refresh ausschalten
                //swipeRefrSemPlan.setRefreshing(false);
            }
        };

        /*if(!Const.HTWEvents.isUserSignedUp(getActivity())) {
            Const.HTWEvents.goToFragment(getActivity(), new HTWDDEventsSignUp());
            return view;
        }*/

        sendRequest(jsonArrayListener, errorListener);



        return view;
    }
    private void sendRequest(Response.Listener<JSONArray> jsonArrayListener, Response.ErrorListener errorListener) {
        JsonArrayRequest jsObjRequest = new JsonArrayRequest("http://www2.htw-dresden.de/~s72743/HTWDDEvents/profile.json", jsonArrayListener, errorListener);
        VolleyDownloader.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
    }

    private void setSemesterPlanView(View view) {
        final TextView userName = (TextView) view.findViewById(R.id.events_profile_user_name);
        final TextView userId = (TextView) view.findViewById(R.id.events_profile_user_id);
        final TextView nickName = (TextView) view.findViewById(R.id.textView2);
        if(userId == null || userName == null || nickName == null ) return;
        userName.setText("dsa");
        userId.setText("dsad");
        nickName.setText("dsa");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onProfileFragmentInteraction(String string);
    }
}
