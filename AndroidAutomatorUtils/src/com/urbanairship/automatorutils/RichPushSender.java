package com.urbanairship.automatorutils;

import android.util.Log;

import java.util.Map;


public class RichPushSender extends PushSender {

    private static final String RICH_PUSH_BROADCAST_URL = "https://go.urbanairship.com/api/airmail/send/broadcast/";
    private static final String RICH_PUSH_URL = "https://go.urbanairship.com/api/airmail/send/";

    /**
     * Constructor for RichPushSender
     * @param masterSecret The specified master secret for the app
     * @param appKey The specified app key for the app
     * @param appName Name of the application
     */
    public RichPushSender(String masterSecret, String appKey) {
        super(masterSecret, appKey, RICH_PUSH_BROADCAST_URL, RICH_PUSH_URL);
    }

    @Override
    protected String createMessage(String pushString, Map<String, String> extras, String uniqueAlertId) {
        StringBuilder builder = new StringBuilder();
        builder.append("{ ");
        if (pushString != null) {
            builder.append(pushString);
        }

        builder.append("\"push\": {\"android\": { \"alert\": \"");
        builder.append(uniqueAlertId);
        builder.append("\",");
        builder.append(createExtrasString(extras));
        builder.append("} }, \"title\": \"Rich Push ");
        builder.append(uniqueAlertId);
        builder.append("\", \"message\": \"Rich Push Message ");
        builder.append(uniqueAlertId);
        builder.append("\",");
        builder.append("\"content-type\": \"text/html\"}");

        return builder.toString();
    }

    /**
     * Sends a rich push message to a user
     * @param user The specified user id to send the rich push message to
     * @return A unique alert Id
     * @throws Exception
     */
    public String sendRichPushToUser(String user) throws Exception {
        Log.i(TAG, "Send message to user: " + user);
        return sendUnicastMessage("\"users\": [\"" + user + "\"],", null);
    }
}
