package igniter.utils;
/**
 * @package com.trioangle.igniter
 * @subpackage utils
 * @category WebServiceUtils
 * @author Trioangle Product Team
 * @version 1.0
 **/

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

import javax.inject.Inject;

import igniter.configs.AppController;

/*****************************************************************
 WebServiceUtils
 ****************************************************************/

public class WebServiceUtils {
    @Inject
    Gson gson;

    public WebServiceUtils() {
        AppController.getAppComponent().inject(this);
    }

    public Object getJsonObjectModel(String jsonString, Object object) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            object = gson.fromJson(jsonObject.toString(), (Class<Object>) object);
        } catch (Exception e) {
            e.printStackTrace();
            return new Object();
        }
        return object;
    }

    public Object getJsonValue(String jsonString, String key, Object object) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            if (jsonObject.has(key)) object = jsonObject.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            return new Object();
        }
        return object;
    }

    public Object getJsonArrayModel(String jsonString, Object object) {
        Object[] objects = null;
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            if (jsonArray.length() > 0) {
                objects = gson.fromJson(jsonArray.toString(), (Class<Object[]>) object);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Object();
        }
        return new java.util.ArrayList(Arrays.asList(objects));
    }
}
