package track;

import org.json.JSONObject;


/**
 * Created by lshw on 17/3/13.
 */

public class TrackMsg {

    public static final String KEY_MSG_TYPE = "msg_type";
    public static final String KEY_TRACK_INFO = "track_info";
    public static final String KEY_TRACK_PNG= "track_png";
    public final int messageType;
    public final JSONObject trackInfo;
    public final byte[] trackPng;

    public TrackMsg(int messageType, JSONObject trackInfo, byte[] trackPng) {
        this.messageType = messageType;
        this.trackInfo = trackInfo;
        this.trackPng = trackPng;
    }

}
