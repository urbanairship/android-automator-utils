package com.urbanairship.automatorutils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class RichPushSenderApiV3 extends PushSenderApiV3 {

    /**
     * Constructor for RichPushSender
     * @param masterSecret The specified master secret for the app
     * @param appKey The specified app key for the app
     * @param appName Name of the application
     */
    public RichPushSenderApiV3(String masterSecret, String appKey) {
        super(masterSecret, appKey);
    }

    @Override
    protected String createMessage(String pushString, String pushValueString, Map<String, String> extras, String uniqueAlertId) throws JSONException {
        JSONObject jsonPayload = new JSONObject();
        if (pushValueString.equalsIgnoreCase("all")) {
            jsonPayload.put(pushString, pushValueString);
        } else {
            JSONObject jsonAudience = new JSONObject();
            JSONObject jsonAudienceType = new JSONObject(pushValueString);
            JSONArray namesArray = jsonAudienceType.names();
            String name = namesArray.getString(0);
            jsonAudience.put(name, jsonAudienceType.get(name));
            jsonPayload.put("audience", jsonAudience);
        }

        JSONArray jsonDeviceType = new JSONArray();
        jsonDeviceType.put("android");
        jsonPayload.put("device_types", jsonDeviceType);

        JSONObject jsonNotification = new JSONObject();
        jsonNotification.put("alert", uniqueAlertId);
        jsonPayload.put("notification", jsonNotification);

        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("title", "Rich Push " + uniqueAlertId);
        jsonMessage.put("body", "Rich Push Message " + uniqueAlertId);
        jsonMessage.put("content_type", "text/html");
        jsonPayload.put("message", jsonMessage);

        return jsonPayload.toString();
    }
}
