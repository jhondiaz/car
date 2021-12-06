package com.webnet.car_meteorologia.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import org.json.JSONException;
import org.json.JSONObject;


public class ApiController extends IntentService {
    private static final String STR_SERVER = "com.webnet.Servics.action.SERVER";
    private static final String ACTION_GET = "com.webnet.Servics.action.GET";
    private static final String ACTION_GETURL = "com.webnet.Servics.action.GETURL";
    private static final String ACTION_POST = "com.webnet.Servics.action.POST";
    private static final String ACTION_POSTRUNT = "com.webnet.Servics.action.POSTRUNT";
    private static final String ACTION_GCM = "com.webnet.Servics.action.GCM";
    private static final String ACTION_DISTANCE = "com.webnet.Servics.action.DISTANCE";
    private static final String ACTION_GEOCODER = "com.webnet.Servics.action.GEOCODER";
    private static final String ACTION_FTB = "com.webnet.Servics.action.FTB";
    private static final String TAG = "ApiController";

    // TODO: Rename parameters
    private static final String EXTRA_TIMEUOT = "com.webnet.Servics.extra.TIMEUOT";
    private static final String EXTRA_PARAM = "com.webnet.Servics.extra.PARAM";
    private static final String EXTRA_APISERVICS = "com.webnet.Servics.extra.APISERVICS";
    private static final String EXTRA_SENDER_ID = "com.webnet.Servics.extra.SENDER_ID";
    private static final String EXTRA_VERSION = "com.webnet.Servics.extra.VERSION";
    private static final String EXTRA_PARAMLAT = "com.webnet.Servics.extra.PARAMLAT";
    private static final String EXTRA_PARAMLNG = "com.webnet.Servics.extra.PARAMLNG";
    private static final String EXTRA_PARAMDL = "com.webnet.Servics.extra.PARAMDL";
    private static final String EXTRA_PARAMOL = "com.webnet.Servics.extra.PARAMOL";

    private static final String EXTRA_PARAMGCM = "com.webnet.Servics.extra.PARAMGCM";
    public static final String EXTRA_DATARESULT = "com.webnet.Servics.extra.DATARESULT";
    private static final String EXTRA_RESULT = "com.webnet.Servics.extra.RESULT";
    public static final String EXTRA_RECEIVER = "com.webnet.Servics.extra.RECEIVER";
    public static final int STATUS_ERROR = -1;
    private static ResultReceiver receiver;

    public ApiController() {
        super("WebNet.ApiController");
    }

    public static void postSendData(Context context, ResultReceiver receiver, JSONObject mRequestParams) {
        Intent intent = new Intent(context, ApiController.class);
        intent.setAction(ACTION_POST);
        intent.putExtra(EXTRA_TIMEUOT, 5000);
        intent.putExtra(EXTRA_RESULT, Result.SendData);
        intent.putExtra(EXTRA_PARAM, mRequestParams.toString());
        intent.putExtra(EXTRA_APISERVICS, ApiRoutes.SendData);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        context.startService(intent);
    }

    public static void getLogin(Context context, ResultReceiver receiver, JSONObject mRequestParams) {
        Intent intent = new Intent(context, ApiController.class);
        intent.setAction(ACTION_GET);
        intent.putExtra(EXTRA_RESULT, Result.Login);
        intent.putExtra(EXTRA_PARAM, getParameter(mRequestParams));
        intent.putExtra(EXTRA_APISERVICS, ApiRoutes.Login);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        context.startService(intent);
    }

    public static void getAlarmar(Context context, ResultReceiver receiver, JSONObject mRequestParams) {
        Intent intent = new Intent(context, ApiController.class);
        intent.setAction(ACTION_GET);
        intent.putExtra(EXTRA_RESULT, Result.GetAlarmas);
        intent.putExtra(EXTRA_PARAM, getParameter(mRequestParams));
        intent.putExtra(EXTRA_APISERVICS, ApiRoutes.GetAlarmas);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            receiver = intent.getParcelableExtra(EXTRA_RECEIVER);

            if (intent != null) {
                final String action = intent.getAction();
                if (ACTION_GET.equals(action)) {

                    final String mparam = intent.getStringExtra(EXTRA_PARAM);
                    final String mapiservics = intent.getStringExtra(EXTRA_APISERVICS);
                    final int TimeOut = intent.getIntExtra(EXTRA_TIMEUOT, 0);
                    final int Result = intent.getIntExtra(EXTRA_RESULT, 0);
                    if (TimeOut != 0) {
                        handleActionGet(mapiservics, mparam, Result);
                    } else {
                        handleActionGet(mapiservics, mparam, Result, TimeOut);
                    }

                } else if (ACTION_POST.equals(action)) {
                    final String mparam = intent.getStringExtra(EXTRA_PARAM);
                    final String mapiservics = intent.getStringExtra(EXTRA_APISERVICS);
                    final int TimeOut = intent.getIntExtra(EXTRA_TIMEUOT, 0);
                    final int Result = intent.getIntExtra(EXTRA_RESULT, 0);
                    if (TimeOut != 0) {
                        handleActionPost(mapiservics, mparam, Result);
                    } else {
                        handleActionPost(mapiservics, mparam, Result, TimeOut);
                    }
                }else if (ACTION_GEOCODER.equals(action)) {
                    final double mparamLAT = intent.getDoubleExtra(EXTRA_PARAMLAT, 0);
                    final double mparamLNG = intent.getDoubleExtra(EXTRA_PARAMLNG, 0);
                    final int Result = intent.getIntExtra(EXTRA_RESULT, 0);
                    handleActionGeoCodder(mparamLAT, mparamLNG, Result);

                } else if (ACTION_DISTANCE.equals(action)) {
                    final int TimeOut = intent.getIntExtra(EXTRA_TIMEUOT, 0);
                    final Location mparamDL = intent.getParcelableExtra(EXTRA_PARAMDL);
                    final Location mparamOL = intent.getParcelableExtra(EXTRA_PARAMOL);
                    final int Result = intent.getIntExtra(EXTRA_RESULT, 0);
                    //handleActionDistancematrix(mparamOL, mparamDL, Result, TimeOut);

                } else if (ACTION_POSTRUNT.equals(action)) {
                    final String mparam = intent.getStringExtra(EXTRA_PARAM);
                    final String mapiservics = intent.getStringExtra(EXTRA_APISERVICS);
                    final int Result = intent.getIntExtra(EXTRA_RESULT, 0);
                    //handleActionPostRunt(mapiservics, new JSONObject(mparam), Result);

                }else if(ACTION_FTB.equals(action)){

                    final String mparam = intent.getStringExtra(EXTRA_PARAM);
                    final String mapiservics = intent.getStringExtra(EXTRA_APISERVICS);
                    final int Result = intent.getIntExtra(EXTRA_RESULT, 0);
                    //handleActionPostTB(mapiservics, new JSONObject(mparam), Result);
                }else if(ACTION_GETURL.equals(action)){

                    final String mparam = intent.getStringExtra(EXTRA_PARAM);
                    final String mapiservics = intent.getStringExtra(EXTRA_APISERVICS);
                    final int TimeOut = intent.getIntExtra(EXTRA_TIMEUOT, 0);
                    final int Result = intent.getIntExtra(EXTRA_RESULT, 0);
                    if (TimeOut != 0) {
                        handleActionGetUrl(mapiservics, mparam, Result);
                    }else{
                        handleActionGetUrl(mapiservics, mparam, Result);
                    }
                }
            }

        } catch (Exception e) {
             //Crashlytics.logException(e);
            return;
        }
    }

    private void handleActionGet(String mapiservics, String param, int Result) {
        Bundle bundle = new Bundle();
        if (!isNetworkAvailable()) {
            bundle.putString(Intent.EXTRA_TEXT, "ERROR DE CONEXION A INTERNET");
            receiver.send(STATUS_ERROR, bundle);
            return;
        }

        try {
            bundle.putString(EXTRA_DATARESULT, new ApiHttp(this, mapiservics).Get(param));
            receiver.send(Result, bundle);
        } catch (JSONException e) {
             //Crashlytics.logException(e);
            bundle.putString(Intent.EXTRA_TEXT, e.getMessage());
            receiver.send(STATUS_ERROR, bundle);
        } catch (Exception e) {
             //Crashlytics.logException(e);
            bundle.putString(Intent.EXTRA_TEXT, e.getMessage());
            receiver.send(STATUS_ERROR, bundle);

        }
    }

    private void handleActionGetUrl(String mapiservics, String param, int Result) {
        Bundle bundle = new Bundle();
        if (!isNetworkAvailable()) {
            bundle.putString(Intent.EXTRA_TEXT, "ERROR DE CONEXION A INTERNET");
            receiver.send(STATUS_ERROR, bundle);
            return;
        }

        try {
            ApiHttp mApiHttp= new ApiHttp(this);
            mApiHttp.setUrl(mapiservics);
            bundle.putString(EXTRA_DATARESULT,  mApiHttp.Get(param));
            receiver.send(Result, bundle);
        } catch (JSONException e) {
             //Crashlytics.logException(e);
            bundle.putString(Intent.EXTRA_TEXT, e.getMessage());
            receiver.send(STATUS_ERROR, bundle);
        } catch (Exception e) {
             //Crashlytics.logException(e);
            bundle.putString(Intent.EXTRA_TEXT, e.getMessage());
            receiver.send(STATUS_ERROR, bundle);

        }
    }

    private void handleActionGet(String mapiservics, String param, int Result, int TimeOut) {
        Bundle bundle = new Bundle();
        if (!isNetworkAvailable()) {
            bundle.putString(Intent.EXTRA_TEXT, "ERROR DE CONEXION A INTERNET");
            receiver.send(STATUS_ERROR, bundle);
            return;
        }
        try {
            bundle.putString(EXTRA_DATARESULT, new ApiHttp(this, mapiservics).Get(param, TimeOut));
            receiver.send(Result, bundle);
        } catch (JSONException e) {
             //Crashlytics.logException(e);
            bundle.putString(Intent.EXTRA_TEXT, e.getMessage());
            receiver.send(STATUS_ERROR, bundle);
        } catch (Exception e) {
             //Crashlytics.logException(e);
            bundle.putString(Intent.EXTRA_TEXT, e.getMessage());
            receiver.send(STATUS_ERROR, bundle);
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionPost(String mapiservics, String param, int Result) {
        Bundle bundle = new Bundle();
        if (!isNetworkAvailable()) {
            bundle.putString(Intent.EXTRA_TEXT, "ERROR DE CONEXION A INTERNET");
            receiver.send(STATUS_ERROR, bundle);
            return;
        }
        try {
            bundle.putString(EXTRA_DATARESULT, new ApiHttp(this, mapiservics).Post(param));
            receiver.send(Result, bundle);
        } catch (JSONException e) {
             //Crashlytics.logException(e);
            bundle.putString(Intent.EXTRA_TEXT, e.getMessage());
            receiver.send(STATUS_ERROR, bundle);
        } catch (Exception e) {
             //Crashlytics.logException(e);
            bundle.putString(Intent.EXTRA_TEXT, e.getMessage());
            receiver.send(STATUS_ERROR, bundle);
        }
    }

    private void handleActionPost(String mapiservics, String param, int Result, int TimeOut) {
        Bundle bundle = new Bundle();
        if (!isNetworkAvailable()) {
            bundle.putString(Intent.EXTRA_TEXT, "ERROR DE CONEXION A INTERNET");
            receiver.send(STATUS_ERROR, bundle);
            return;
        }
        try {
            bundle.putString(EXTRA_DATARESULT, new ApiHttp(this, mapiservics).Post(param, TimeOut));
            receiver.send(Result, bundle);
        } catch (JSONException e) {
             //Crashlytics.logException(e);
            bundle.putString(Intent.EXTRA_TEXT, e.getMessage());
            receiver.send(STATUS_ERROR, bundle);
        } catch (Exception e) {
             //Crashlytics.logException(e);
            bundle.putString(Intent.EXTRA_TEXT, e.getMessage());
            receiver.send(STATUS_ERROR, bundle);
            return;
        }
    }

    private void handleActionGeoCodder(double Latitude, double Longitude, int Result) {
        Bundle bundle = new Bundle();
        if (!isNetworkAvailable()) {
            bundle.putString(Intent.EXTRA_TEXT, "ERROR DE CONEXION A INTERNET");
            receiver.send(STATUS_ERROR, bundle);
            return;
        }
        try {
            bundle.putString(EXTRA_DATARESULT, new ApiHttp(this).GetGeocoder(Latitude, Longitude));
            receiver.send(Result, bundle);
        } catch (JSONException e) {
             //Crashlytics.logException(e);
            bundle.putString(Intent.EXTRA_TEXT, e.getMessage());
            receiver.send(STATUS_ERROR, bundle);
        } catch (Exception e) {
             //Crashlytics.logException(e);
            bundle.putString(Intent.EXTRA_TEXT, e.getMessage());
            receiver.send(STATUS_ERROR, bundle);
        }
    }

    private static String getParameter(JSONObject jsonParam) {
        StringBuilder Parameter = new StringBuilder();
        try {

            for (int i = 0; i < jsonParam.names().length(); i++) {
                if (i != 0) {
                    Parameter.append("&");
                }
                Parameter.append(jsonParam.names().getString(i));
                Parameter.append("=");
                Parameter.append(Uri.encode(jsonParam.get(jsonParam.names().getString(i)).toString(), "utf-8"));
            }
        } catch (JSONException e) {
             //Crashlytics.logException(e);
        }
        return Parameter.toString();
    }


    public class Result {
        public static final int Login = 0;
        public static final int SendData = 1;
        public static final int GetAlarmas = 2;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
