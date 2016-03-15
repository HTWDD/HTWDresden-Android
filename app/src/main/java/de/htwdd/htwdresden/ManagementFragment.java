package de.htwdd.htwdresden;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
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
import de.htwdd.htwdresden.database.DatabaseManager;
import de.htwdd.htwdresden.database.SemesterPlanDAO;
import de.htwdd.htwdresden.interfaces.INavigation;
import de.htwdd.htwdresden.types.semesterplan.FreeDay;
import de.htwdd.htwdresden.types.semesterplan.SemesterPlan;


/**
 * Fragement zur Übersicht aller wichtigen Uni-Einrichtungen
 */
public class ManagementFragment extends Fragment {

    public static final String SHARED_PREFS_SEMESTER_PLAN_JSON = "de.htwdd.htwdresden.SemestePlanJSON";
    public static final String SHARED_PREFS_CATCH_DATE = "de.htwdd.htwdresden.catchDate";

    public ManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_management, container, false);
        final SwipeRefreshLayout swipeRefrSemPlan = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefr_SemPlan);
        final SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        //Hole das JSON-Objekt aus Const.SEMESTERPLAN_URL_JSON und initialisiere das Objekt von SemesterPlan
        final Response.Listener<JSONArray> jsonArrayListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                boolean foundActualSemesterplan = false;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject semesterPlanJSON = response.getJSONObject(i);
                        SemesterPlan semesterPlan = new SemesterPlan(semesterPlanJSON);

                        final SemesterPlanDAO semesterPlanDAO = new SemesterPlanDAO(new DatabaseManager(getActivity()));
                        semesterPlanDAO.addSemesterPlan(semesterPlan);

                        if (semesterPlan.isThisSemester(SemesterPlan.getActualYear(), SemesterPlan.getActualSemester())) {
                            long currentTime = System.currentTimeMillis();
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putLong(SHARED_PREFS_CATCH_DATE, currentTime);
                            editor.apply();
                            setSemesterplanviewText(semesterPlan, getView());

                            foundActualSemesterplan=true;
                        }
                    } catch (JSONException e) {
                        Log.e("JSON SEMESTERPLAN", "JSON IS BROKEN");
                        Log.e(e.getClass().getName(), e.getMessage() + " ", e);
                        Snackbar.make(view, R.string.info_error, Snackbar.LENGTH_LONG).show();
                    }
                }
                if(!foundActualSemesterplan) Snackbar.make(view, R.string.info_error_semesterlan_outofdate, Snackbar.LENGTH_LONG).show();
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
                swipeRefrSemPlan.setRefreshing(false);
            }
        };

        swipeRefrSemPlan.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        sendRequestToGetSemesterplan(jsonArrayListener, errorListener);
                        swipeRefrSemPlan.setRefreshing(false);
                    }
                }
        );

        // Setze Toolbartitle
        ((INavigation) getActivity()).setTitle(getResources().getString(R.string.navi_uni_administration));

        CardView management_office = (CardView) view.findViewById(R.id.management_office);
        management_office.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.htw-dresden.de/hochschule/zentrale-verwaltung-dezernate/dezernat-studienangelegenheiten/studentensekretariat.html"));
                getActivity().startActivity(browserIntent);
            }
        });

        CardView management_printing = (CardView) view.findViewById(R.id.management_printing);
        management_printing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.htw-dresden.de/intern/technik-druck-it-dienste/drucken-kopieren.html"));
                getActivity().startActivity(browserIntent);
            }
        });

        CardView management_examination_office = (CardView) view.findViewById(R.id.management_examination_office);
        management_examination_office.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.htw-dresden.de/hochschule/zentrale-verwaltung-dezernate/dezernat-studienangelegenheiten/pruefungsamt.html"));
                getActivity().startActivity(browserIntent);
            }
        });

        CardView management_stura = (CardView) view.findViewById(R.id.management_stura);
        management_stura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.stura.htw-dresden.de/"));
                getActivity().startActivity(browserIntent);
            }
        });

        if (!sharedPref.contains(SHARED_PREFS_CATCH_DATE)) {
            sendRequestToGetSemesterplan(jsonArrayListener, errorListener);
        } else {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - sharedPref.getLong(SHARED_PREFS_CATCH_DATE, -1)) >= Const.semesterPlanUpdater.UPDATE_INTERVAL) {
                sendRequestToGetSemesterplan(jsonArrayListener, errorListener);
            } else {
                updateSemplanviewFromLocalsource(view);
            }
        }
        return view;
    }

    private void updateSemplanviewFromLocalsource(View view) {
        try {
            final SemesterPlanDAO semesterPlanDAO = new SemesterPlanDAO(new DatabaseManager(getActivity()));
            SemesterPlan semesterPlan = semesterPlanDAO.getSemsterplan(SemesterPlan.getActualYear(), SemesterPlan.getActualSemester());
            setSemesterplanviewText(semesterPlan, view);
        } catch (Exception e) {
            Log.e("SEMESTERPLAN", "SQL ERROR");
            Log.e(e.getClass().getName(), e.getMessage() + " ", e);
            //TODO BEI EINEM FEHLER BEIM LESEN AUS DER LOKALEN DATENBANK DIE DATEN NOCHMAL AUS INTERNET HOLEN ?
            //TODO snackbar stürtzt mit NullPointerException ab ( view ? )
            //Snackbar.make(view, R.string.info_error, Snackbar.LENGTH_LONG).show();
        }
    }

    private void setSemesterplanviewText(SemesterPlan semesterPlan, View view) {
        if (semesterPlan == null) return;
        final TextView semesterplanBezeichnung = (TextView) view.findViewById(R.id.semesterplan_Bezeichnung);
        final TextView semesterplanLecturePeriod = (TextView) view.findViewById(R.id.semesterplan_lecturePeriod);
        final TextView semesterplanFreieTage = (TextView) view.findViewById(R.id.semesterplan_freieTage);
        final TextView semesterplanFreieTageNamen = (TextView) view.findViewById(R.id.semesterplan_freieTageNamen);
        final TextView semesterplanPruefPeriod = (TextView) view.findViewById(R.id.semesterplan_pruefPeriod);
        final TextView semesterplanRegistration = (TextView) view.findViewById(R.id.semesterplan_reregistration);

        semesterplanBezeichnung.setText(semesterPlan.getBezeichnung(getString(R.string.semesterplan_wintersemester), getString(R.string.semesterplan_sommersemester)));
        semesterplanLecturePeriod.setText(semesterPlan.getLecturePeriod().toString());
        semesterplanPruefPeriod.setText(semesterPlan.getExamsPeriod().toString());
        semesterplanRegistration.setText(semesterPlan.getReregistration().toString());

        semesterplanFreieTageNamen.setText("");
        semesterplanFreieTage.setText("");
        if (semesterPlan.getFreeDays() == null) return;
        for (FreeDay f : semesterPlan.getFreeDays()) {
            semesterplanFreieTageNamen.append(f.getName());
            semesterplanFreieTage.append(f.toString());
        }


    }

    private void sendRequestToGetSemesterplan(Response.Listener<JSONArray> jsonArrayListener, Response.ErrorListener errorListener) {
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Const.semesterPlanUpdater.SEMESTERPLAN_URL_JSON, jsonArrayListener, errorListener);
        VolleyDownloader.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
    }

}
