package com.aadamsdev.communities.request;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;

public class GenericRequest<T> extends JsonRequest<T> {

    private static final Gson gson = new Gson();
    private final Class _class;
    private final Response.Listener<T> listener;
    private int method;
    private String url;
    private String requestBody;
    private static final String PROTOCOL_CHARSET = "utf-8";
    private static final String TAG = "ObjectRequest";
    private Map<String, String> headers;
    private Map<String, Object> params;

    public GenericRequest(int method, String url, Map<String, Object> params, Class _class,
                          Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, gson.toJson(params), listener, errorListener);
        this._class = _class;
        this.listener = listener;
        if (params != null) {
            this.params = params;
            requestBody = gson.toJson(params);
        }
        this.method = method;
        this.url = url;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public String getUrl() {
        if (method == Request.Method.GET) {
            StringBuilder stringBuilder = new StringBuilder(url);
            if (params != null) {
                Iterator<Map.Entry<String, Object>> iterator = params.entrySet().iterator();
                int i = 1;
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> entry = iterator.next();
                    if (i == 1) {
                        stringBuilder.append("?" + entry.getKey() + "=" + entry.getValue());
                    } else {
                        stringBuilder.append("&" + entry.getKey() + "=" + entry.getValue());
                    }
                    iterator.remove(); // avoids a ConcurrentModificationException
                    i++;
                }
                url = stringBuilder.toString();
            }
        }
        return url;
    }

    @Override
    public byte[] getBody() {
        try {
            return requestBody == null ? null : requestBody.getBytes("utf-8");
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
            return null;
        }
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            if (_class.getName().equals(JSONObject.class.getName())) {
                try {
                    JSONObject object = new JSONObject(json);
                    return Response.success(object, HttpHeaderParser.parseCacheHeaders(response));
                } catch (JSONException ex) {
                    return Response.error(new ParseError(ex));
                }
            } else if (_class.getName().equals(JSONArray.class.getName())) {
                try {
                    JSONArray object = new JSONArray(json);
                    return Response.success(object, HttpHeaderParser.parseCacheHeaders(response));
                } catch (JSONException ex) {
                    return Response.error(new ParseError(ex));
                }
            } else {
                return Response.success(gson.fromJson(json, _class), HttpHeaderParser.parseCacheHeaders(response));
            }
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage());
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (headers == null || headers.isEmpty()) {
            return super.getHeaders();
        } else {
            return headers;
        }
    }
}