/*
 * Copyright 2013 Urban Airship
 */

package com.urbanairship.automatorutils;

import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Helper class to send push notifications
 *
 */
public class PushSender {
    private final String masterSecret;
    private final String appKey;
    private final String broadcastUrl;
    private final String pushUrl;

    protected static String TAG = "PushSender";

    private static int MAX_SEND_MESG_RETRIES = 3;
    private static int SEND_MESG_RETRY_DELAY = 3000;  // 3 seconds

    private static final String PUSH_BROADCAST_URL = "https://go.urbanairship.com/api/push/broadcast/";
    private static final String PUSH_URL = "https://go.urbanairship.com/api/push/";


    /**
     * Constructor for PushSender
     * @param masterSecret The specified master secret for the app
     * @param appKey The specified app key for the app
     */
    public PushSender(String masterSecret, String appKey) {
        this(masterSecret, appKey, PUSH_BROADCAST_URL, PUSH_URL);
    }

    /**
     * Constructor for PushSender
     * @param masterSecret The specified master secret for the app
     * @param appKey The specified app key for the app
     * @param broadcastUrl The url for broadcasting push messages
     * @param pushUrl The url for push messages
     */
    protected PushSender(String masterSecret, String appKey, String broadcastUrl, String pushUrl) {
        this.masterSecret = masterSecret;
        this.appKey = appKey;
        this.broadcastUrl = broadcastUrl;
        this.pushUrl = pushUrl;
    }

    /**
     * Builds the message to be sent
     * @param pushString The string to append based on the type of push (user, alias, tag)
     * @param activity The specified activity to send the push message to
     * @param uniqueAlertId The string used to identify push messages
     * @return The message to be sent
     */
    protected String createMessage(String pushString, Map<String, String> extras, String uniqueAlertId) {
        StringBuilder builder = new StringBuilder();
        builder.append("{ ");
        if (pushString != null) {
            builder.append(pushString);
        }
        builder.append("\"android\": { \"alert\": \"");
        builder.append(uniqueAlertId);
        builder.append("\",");
        builder.append(createExtrasString(extras));
        builder.append("}");

        return builder.toString();
    }


    /**
     * Broadcast a push message
     * @return A unique alert Id
     * @throws Exception
     */
    public String sendPushMessage() throws Exception {
        Log.i(TAG, "Broadcast message");
        return sendBroadcastMessage(null);
    }

    /**
     * Sends a push message to an activity
     * @param extras Any notification extras
     * @return A unique alert Id
     * @throws Exception
     */
    public String sendPushMessage(Map<String, String> extras) throws Exception {
        Log.i(TAG, "Broadcast message: to activity");
        return sendBroadcastMessage(extras);
    }

    /**
     * Sends a push message to a tag
     * @param tag The specified tag to send the push message to
     * @return A unique alert Id
     * @throws Exception
     */
    public String sendPushToTag(String tag) throws Exception {
        Log.i(TAG, "Send message to tag: " + tag);
        return sendUnicastMessage("\"tags\": [\"" + tag + "\"],", null);
    }

    /**
     * Sends a push message to an alias
     * @param alias The specified alias to send the push message to
     * @return A unique alert Id
     * @throws Exception
     */
    public String sendPushToAlias(String alias) throws Exception {
        Log.i(TAG, "Send message to tag: " + alias);
        return sendUnicastMessage("\"aliases\": [\"" + alias + "\", \"anotherAlias\"],", null);
    }

    /**
     * Sends a push message to an APID
     * @param apid The specified apid to send the push message to
     * @return A unique alert Id
     * @throws Exception
     */
    public String sendPushToApid(String apid) throws Exception {
        Log.i(TAG, "Send message to apid: " + apid);
        return sendUnicastMessage("\"apids\": [\"" + apid + "\"],", null);
    }

    /**
     * Sends a unicast message
     * @param extras Any notification extras
     * @return A unique alert Id
     * @throws Exception
     */
    protected String sendUnicastMessage(String pushString, Map<String, String> extras) throws Exception {
        return sendMessage(pushUrl, pushString, extras);
    }

    /**
     * Sends a broadcast message
     * @param extras Any notification extras
     * @return A unique alert Id
     * @throws Exception
     */
    protected String sendBroadcastMessage(Map<String, String> extras) throws Exception {
        return sendMessage(broadcastUrl, null, extras);
    }

    /**
     * Creates the extras json string from a map
     * @param extras Map of the extras
     * @return The extras string
     */
    protected String createExtrasString(Map<String, String> extras) {
        StringBuilder builder = new StringBuilder();
        builder.append("\"extra\": {");

        if (extras != null) {

            Iterator<Entry<String, String>> entries = extras.entrySet().iterator();

            while (entries.hasNext()) {
                Entry<String, String> entry = entries.next();

                builder.append("\"");
                builder.append(entry.getKey());
                builder.append("\": \"");
                builder.append(entry.getValue());
                builder.append("\"");

                if (entries.hasNext()) {
                    builder.append(",");
                }
            }
        }

        builder.append("}");
        return builder.toString();
    }

    /**
     * Actually sends the push message
     * @param urlString The specified url the message is sent to
     * @param pushString The specified type of push
     * @param extras Any notification extras
     * @return A unique alert Id
     * @throws Exception
     */
    private String sendMessage(String urlString, String pushString, Map<String, String> extras) throws Exception {
        int sendMesgRetryCount = 0;
        String uniqueAlertId = "uniqueAlertId";
        while ( sendMesgRetryCount < MAX_SEND_MESG_RETRIES ) {
            uniqueAlertId = AutomatorUtils.generateUniqueAlertId();
            String json = createMessage(pushString, extras, uniqueAlertId);
            Log.i(TAG,  "Created message to send" + json);

            try {
                sendMessageHelper(urlString, json);
                return uniqueAlertId;
            } catch (Exception ex) {
                Log.e(TAG, "Failed to send message: " + json, ex);
                Thread.sleep(SEND_MESG_RETRY_DELAY);
                sendMesgRetryCount++;
            }
        }
        return uniqueAlertId;
    }

    /**
     * Actually sends the push message
     * @param urlString The specified url the message is sent to
     * @param message The json formatted message to be sent
     * @throws IOException
     */
    private void sendMessageHelper(String urlString, String message) throws IOException  {
        URL url = new URL(urlString);
        HttpURLConnection conn = null;

        String basicAuthString =  "Basic "+Base64.encodeToString(String.format("%s:%s", appKey, masterSecret).getBytes(), Base64.NO_WRAP);

        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(false);
            conn.setRequestProperty("Content-Type",
                    "application/json");

            conn.setRequestProperty("Authorization", basicAuthString);

            // Create the form content
            OutputStream out = conn.getOutputStream();
            Writer writer = new OutputStreamWriter(out, "UTF-8");
            writer.write(message);
            writer.close();
            out.close();

            if (conn.getResponseCode() != 200) {
                Log.e(TAG, "Sending push failed with: " + conn.getResponseCode() + " " + conn.getResponseMessage() + " Message: " + message);
                throw new IOException(conn.getResponseMessage());
            } else {
                Log.i(TAG, "Push sent: " + message);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
