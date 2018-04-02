package com.aadamsdev.communities.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class HttpUtils {
    private final static String TAG = HttpUtils.class.getSimpleName();
    private final static String baseUrlEmulator = "http://10.0.2.2:8080"; // TODO: LOCALHOST EMULATOR
    private final static String baseUrl = "http://192.168.0.10:8090"; // TODO: LOCALHOST EMULATOR


}