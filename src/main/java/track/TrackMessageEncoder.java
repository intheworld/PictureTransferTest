package track;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.util.Base64;
import org.json.JSONObject;

import java.nio.charset.Charset;

/**
 * Created by lshw on 17/3/13.
 */

public class TrackMessageEncoder extends ProtocolEncoderAdapter {
    public static final String TAG = TrackMessageEncoder.class.getSimpleName();

    public void encode(IoSession session, Object message,
                       ProtocolEncoderOutput out) throws Exception {
        TrackMsg msg = (TrackMsg) message;
        System.out.println("track png" + msg.trackPng.length);
        byte[] pngBase64 = Base64.encodeBase64(msg.trackPng);
        System.out.println("png base64 length = " + pngBase64.length);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(TrackMsg.KEY_MSG_TYPE, msg.messageType);
        jsonObject.put(TrackMsg.KEY_TRACK_INFO, msg.trackInfo);
        jsonObject.put(TrackMsg.KEY_TRACK_PNG, new String(pngBase64, Charset.forName("UTF-8")));
        IoBuffer buf = IoBuffer.allocate(200000).setAutoExpand(true);
        buf.putInt(jsonObject.toString().getBytes().length + 4);
        System.out.println("message length = " + jsonObject.toString().getBytes().length);
        //System.out.println("message = " + jsonObject.toString());
        buf.put(jsonObject.toString().getBytes());
        buf.flip();
        out.write(buf);
        out.flush();
    }

}