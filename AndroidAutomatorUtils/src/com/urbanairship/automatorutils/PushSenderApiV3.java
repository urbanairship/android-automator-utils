/*
 * Copyright 2013 Urban Airship
 */

package com.urbanairship.automatorutils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class to send push notifications
 *
 */
public class PushSenderApiV3 extends PushSender {
    private static final String PUSH_URL = "https://go.urbanairship.com/api/push/";
    private HashMap<String, String> requestProperties = new HashMap<String, String>();

    /**
     * Constructor for PushSender
     * @param masterSecret The specified master secret for the app
     * @param appKey The specified app key for the app
     */
    public PushSenderApiV3(String masterSecret, String appKey) {
        super(masterSecret, appKey, PUSH_URL);
        requestProperties.put("Accept", "application/vnd.urbanairship+json; version=3;");
    }

    @Override
    protected String createMessage(String pushString, String pushValueString, Map<String, String> extras, String uniqueAlertId) throws JSONException {
        Log.i(TAG, "PushSenderApiV3 createMessage() pushString: " + pushString + " pushValueString: " + pushValueString);
        JSONObject jsonPayload = new JSONObject();
        if (pushValueString.equalsIgnoreCase("all")) {
            jsonPayload.put(pushString, pushValueString);
        } else {
            JSONObject jsonAudience = new JSONObject();
            JSONObject jsonAudienceType = new JSONObject(pushValueString);
            JSONArray namesArray = jsonAudienceType.names();
            String name = namesArray.getString(0);
            Log.i(TAG, "PushSenderAPiV3 name: " + name);
            jsonAudience.put(name, jsonAudienceType.get(name));
            Log.i(TAG, "PushSenderApiV3 jsonAudience: " + jsonAudience.toString());
            jsonPayload.put("audience", jsonAudience);
        }

        JSONArray jsonDeviceType = new JSONArray();
        jsonDeviceType.put("android");
        jsonPayload.put("device_types", jsonDeviceType);

        JSONObject jsonNotification = new JSONObject();
        jsonNotification.put("alert", uniqueAlertId);
        jsonPayload.put("notification", jsonNotification);

        return jsonPayload.toString();
    }
    /**
     * Broadcast a push message
     * @return A unique alert Id
     * @throws Exception
     */
    @Override
    public String sendPushMessage() throws Exception {
        Log.i(TAG, "Broadcast message");
        return sendMessage(PUSH_URL, "audience", "all", null, requestProperties);
    }

    /**
     * Sends a push message to an activity
     * @param extras Any notification extras
     * @return A unique alert Id
     * @throws Exception
     */
    @Override
    public String sendPushMessage(Map<String, String> extras) throws Exception {
        Log.i(TAG, "Broadcast message: to activity");
        return sendMessage(PUSH_URL, "audience", "all", extras, requestProperties);
    }

    /**
     * Sends a push message to a tag
     * @param tag The specified tag to send the push message to
     * @return A unique alert Id
     * @throws Exception
     */
    @Override
    public String sendPushToTag(String tag) throws Exception {
        Log.i(TAG, "Send message to tag: " + tag);
        JSONObject jsonAudience = new JSONObject();
        jsonAudience.put("tag", tag);
        return sendMessage(PUSH_URL, "audience", jsonAudience.toString(), null, requestProperties);
    }

    /**
     * Sends a push message to an alias
     * @param alias The specified alias to send the push message to
     * @return A unique alert Id
     * @throws Exception
     */
    @Override
    public String sendPushToAlias(String alias) throws Exception {
        Log.i(TAG, "Send message to tag: " + alias);
        JSONObject jsonAudience = new JSONObject();
        jsonAudience.put("alias", alias);
        return sendMessage(PUSH_URL, "audience", jsonAudience.toString(), null, requestProperties);
    }

    /**
     * Sends a push message to an APID
     * @param apid The specified apid to send the push message to
     * @return A unique alert Id
     * @throws Exception
     */
    @Override
    public String sendPushToApid(String apid) throws Exception {
        Log.i(TAG, "Send message to apid: " + apid);
        JSONObject jsonAudience = new JSONObject();
        jsonAudience.put("apid", apid);
        return sendMessage(PUSH_URL, "audience", jsonAudience.toString(), null, requestProperties);
    }
}
