package com.urbanairship.automatorutils;

import android.util.Log;

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
    protected String createMessage(String pushString, Map<String, String> extras, String uniqueAlertId) {
        StringBuilder builder = new StringBuilder();
        builder.append("{ ");
        if (pushString != null) {
            builder.append(pushString);
        }

        builder.append("\"device_types\": [ \"android\" ],");
        builder.append("\"notification\": { \"alert\": \"");
        builder.append(uniqueAlertId);
        builder.append("\"},");
        builder.append("\"message\": { ");
        builder.append("\"title\": \"Rich Push ");
        builder.append(uniqueAlertId);
        builder.append("\", \"body\": \"Rich Push Message ");
        builder.append(uniqueAlertId);
        builder.append("\",");
        builder.append(createExtrasString(extras));
        builder.append(",");
        builder.append("\"content_type\": \"text/html\"}}");

        Log.i(TAG, "RichPushSender API v3 string is: " + builder.toString());
        return builder.toString();
    }
}
