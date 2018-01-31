package com.aadamsdev.communities.utils;

import android.content.Context;

import com.aadamsdev.communities.result.BasicResult;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

/**
 * Created by Andrew Adams on 1/28/2018.
 */
public class HttpHelper {
    private static HttpHelper instance;
    private RequestQueue requestQueue;
    private static Context context;

    private final static String TAG = HttpHelper.class.getSimpleName();

    private HttpHelper(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized HttpHelper getInstance(Context context) {
        if (instance == null) {
            instance = new HttpHelper(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            return requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }
}