package com.frand.easyandroid.socket;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.frand.easyandroid.log.FFLogger;

import android.net.http.AndroidHttpClient;
import android.os.Looper;
import android.text.TextUtils;
import android.util.SparseArray;

public class SocketIOClient {
    public static interface Handler {
        public void onConnect();

        public void onConnectToEndpoint(String endpoint);

        public void on(String event, JSONArray arguments);

        public void onDisconnect(int code, String reason);

        public void onJSON(JSONObject json);

        public void onMessage(String message);

        public void onError(Exception error);
    }

    public static interface Acknowledge {
        public void acknowledge(String[] args);
    }

    private static final String TAG = "SocketIOClient";

    String mURL;
    Handler mHandler;
    String mSession;
    int mHeartbeat;
    WebSocketClient mClient;
    String mEndpoint;
    private AtomicInteger mMessageIdCounter;
    private SparseArray<Acknowledge> mAcknowledges;

    public SocketIOClient(URI uri, Handler handler) {
        this(uri, handler, null);
    }

    public SocketIOClient(URI uri, Handler handler, String namespace) {
        mEndpoint = namespace;
        mAcknowledges = new SparseArray<SocketIOClient.Acknowledge>();
        mMessageIdCounter = new AtomicInteger(0);
        if (TextUtils.isEmpty(namespace)) {
            mEndpoint = "socket.io";
        }

        // remove trailing "/" from URI, in case user provided e.g.
        // http://test.com/
        mURL = uri.toString().replaceAll("/$", "") + "/" + mEndpoint + "/1/";
        mHandler = handler;
    }

    private static String downloadUriAsString(final HttpUriRequest req) throws IOException {
        AndroidHttpClient client = AndroidHttpClient.newInstance("android-websockets");
        try {
            HttpResponse res = client.execute(req);
            return readToEnd(res.getEntity().getContent());
        } finally {
            client.close();
        }
    }

    private int getNextMessageId() {
        return mMessageIdCounter.incrementAndGet();
    }

    private static byte[] readToEndAsArray(InputStream input) throws IOException {
        DataInputStream dis = new DataInputStream(input);
        byte[] stuff = new byte[1024];
        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        int read = 0;
        while ((read = dis.read(stuff)) != -1) {
            buff.write(stuff, 0, read);
        }

        return buff.toByteArray();
    }

    private static String readToEnd(InputStream input) throws IOException {
        return new String(readToEndAsArray(input));
    }

    android.os.Handler mSendHandler;
    Looper mSendLooper;

    public void emit(String name, JSONArray args) throws JSONException {
        emit(name, args, null);
    }

    public void emit(String name, JSONArray args, final Acknowledge acknowledge) throws JSONException {
        final JSONObject event = new JSONObject();
        event.put("name", name);
        event.put("args", args);

        final int nextId = getNextMessageId();
        if (acknowledge != null) {
            mAcknowledges.put(nextId, acknowledge);
        }
        mSendHandler.post(new Runnable() {
            @Override
            public void run() {
                mClient.send(String.format("5:" + nextId + (acknowledge == null ? "" : "+") +"::%s", event.toString()));
            }
        });
    }

    public void emit(final JSONObject jsonMessage) throws JSONException {
        emit(jsonMessage, null);
    }

    public void emit(final JSONObject jsonMessage, final Acknowledge acknowledge) throws JSONException {

        final int nextId = getNextMessageId();
        if (acknowledge != null) {
            mAcknowledges.put(nextId, acknowledge);
        }
        mSendHandler.post(new Runnable() {

            @Override
            public void run() {
                mClient.send(String.format("4:" + nextId + (acknowledge == null ? "" : "+") + "::%s", jsonMessage.toString()));
            }
        });
    }

    public void emit(final String message) {
        emit(message, (Acknowledge) null);
    }

    public void emit(final String message, final Acknowledge acknowledge) {

        final int nextId = getNextMessageId();
        if (acknowledge != null) {
            mAcknowledges.put(nextId, acknowledge);
        }
        mSendHandler.post(new Runnable() {

            @Override
            public void run() {
                mClient.send(String.format("3:" + nextId + (acknowledge == null ? "" : "+") +"::%s", message));
            }
        });
    }

    private void connectSession() throws URISyntaxException {
        mClient = new WebSocketClient(new URI(mURL + "websocket/" + mSession), new WebSocketClient.Listener() {
            @Override
            public void onMessage(byte[] data) {
                cleanup();
                mHandler.onError(new Exception("Unexpected binary data"));
            }

            @Override
            public void onMessage(String message) {
                try {
                	FFLogger.d(TAG, "Message: " + message);
                    String[] parts = message.split(":", 4);
                    int code = Integer.parseInt(parts[0]);
                    switch (code) {
                    case 1:
                        // connect
                        if (!TextUtils.isEmpty(parts[2])) {
                            mHandler.onConnectToEndpoint(parts[2]);
                        } else {
                            mHandler.onConnect();
                        }
                        break;
                    case 2:
                        // heartbeat
                        mClient.send("2::");
                        break;
                    case 3: {
                        // message
                        final String messageId = parts[1];
                        final String dataString = parts[3];

                        if (!"".equals(messageId)) {
                            mSendHandler.post(new Runnable() {

                                @Override
                                public void run() {
                                    mClient.send(String.format("6:::%s", messageId));
                                }
                            });
                        }
                        mHandler.onMessage(dataString);
                        break;
                    }
                    case 4: {
                        // json message
                        final String messageId = parts[1];
                        final String dataString = parts[3];

                        JSONObject jsonMessage = null;

                        try {
                            jsonMessage = new JSONObject(dataString);
                        } catch (JSONException e) {
                            jsonMessage = new JSONObject();
                        }
                        if (!"".equals(messageId)) {
                            mSendHandler.post(new Runnable() {

                                @Override
                                public void run() {
                                    mClient.send(String.format("6:::%s", messageId));
                                }
                            });
                        }
                        mHandler.onJSON(jsonMessage);
                        break;
                    }
                    case 5: {
                        final String messageId = parts[1];
                        final String dataString = parts[3];
                        JSONObject data = new JSONObject(dataString);
                        String event = data.getString("name");
                        JSONArray args;
                        try {
                            args = data.getJSONArray("args");
                        } catch (JSONException e) {
                            args = new JSONArray();
                        }
                        if (!"".equals(messageId)) {
                            mSendHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mClient.send(String.format("6:::%s", messageId));
                                }
                            });
                        }
                        mHandler.on(event, args);
                        break;
                    }
                    case 6:
                        // ACK
                        if (parts[3] != null && parts[3].contains("+")) {
                            String[] ackParts = parts[3].split("\\+");
                            int ackId = Integer.valueOf(ackParts[0]);

                            String ackArgs = ackParts[1];

                            int startIndex = ackArgs.indexOf('[') + 1;

                            ackArgs = ackArgs.substring(startIndex, ackArgs.length() - 1);

                            Acknowledge acknowledge = mAcknowledges.get(ackId);

                            if (acknowledge != null) {

                                String[] params = ackArgs.split(",");
                                for (int i = 0; i < params.length; i++) {
                                    params[i] = params[i].replace("\"", "");
                                }
                                acknowledge.acknowledge(params);
                            }

                            mAcknowledges.remove(ackId);
                        }
                        break;
                    case 7:
                        // error
                        throw new Exception(message);
                    case 8:
                        // noop
                        break;
                    default:
                        throw new Exception("unknown code");
                    }
                } catch (Exception ex) {
                    onError(ex);
                }
            }

            @Override
            public void onError(Exception error) {
                cleanup();
                mHandler.onError(error);
            }

            @Override
            public void onDisconnect(int code, String reason) {
                cleanup();
                // attempt reconnect with same session?
                mHandler.onDisconnect(code, reason);
            }

            @Override
            public void onConnect() {
                mSendHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSendHandler.postDelayed(this, mHeartbeat);
                        mClient.send("2:::");
                    }
                }, mHeartbeat);
            }
        }, null);
        mClient.connect();
    }

    public void disconnect() throws IOException {
        cleanup();
    }

    private void cleanup() {
        if (mClient != null)
            mClient.disconnect();
        mClient = null;
        mMessageIdCounter.set(0);
        mAcknowledges.clear();
        mSendLooper.quit();
        mSendLooper = null;
        mSendHandler = null;
    }

    public void connect() {
        if (mClient != null)
            return;
        new Thread() {
            public void run() {
                HttpPost post = new HttpPost(mURL);
                try {
                    String line = downloadUriAsString(post);
                    String[] parts = line.split(":");
                    mSession = parts[0];
                    String heartbeat = parts[1];
                    if (!"".equals(heartbeat))
                        mHeartbeat = Integer.parseInt(heartbeat) / 2 * 1000;
                    String transportsLine = parts[3];
                    String[] transports = transportsLine.split(",");
                    HashSet<String> set = new HashSet<String>(Arrays.asList(transports));
                    if (!set.contains("websocket"))
                        throw new Exception("websocket not supported");

                    Looper.prepare();
                    mSendLooper = Looper.myLooper();
                    mSendHandler = new android.os.Handler();

                    connectSession();

                    Looper.loop();
                } catch (Exception e) {
                    mHandler.onError(e);
                }
            };
        }.start();
    }

    /**
     * Connect to an endpoint
     */
    public void connectToEndpoint(final String endpoint) {

        if (mClient.isConnected() && !TextUtils.isEmpty(endpoint)) {
            mEndpoint = endpoint;
            mSendHandler.post(new Runnable() {

                @Override
                public void run() {
                    mClient.send("1::" + endpoint);
                }
            });
        }
    }

    /**
     * Disconnect from an endpoint or socket
     * 
     * @param endpoint
     *            {@code null} to disconnect the entire socket, otherwise the
     *            endpoint to disconnect from
     */
    public void sendDisconnect(final String endpoint) {

        if (TextUtils.isEmpty(endpoint)) {

            mSendHandler.post(new Runnable() {

                @Override
                public void run() {
                    mClient.send("0");
                }
            });
        }

        else {
            mSendHandler.post(new Runnable() {

                @Override
                public void run() {
                    mClient.send("0::" + endpoint);
                }
            });
        }
    }

    /**
     * Get the current connected endpoint
     * 
     * @return The current connected endpoint, "socket.io" if connected to the
     *         default endpoint
     */
    public String getConnectedEndpoint() {
        return mEndpoint;
    }
}
