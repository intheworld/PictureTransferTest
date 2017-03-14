package track;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
/**
 * Created by lshw on 17/3/13.
 */

public class TrackMessageDecoder extends CumulativeProtocolDecoder {

        @Override
        protected boolean doDecode(IoSession session, IoBuffer in,
                                   ProtocolDecoderOutput out) throws Exception {

            if(in.remaining()<4)
            {
                return false;
            }
            if (in.remaining() > 1) {

                in.mark();
                int length = in.getInt();

                if(length - 4 > in.remaining()){
                    System.out.println("package not enough  left=" + in.remaining() + " length=" + length);
                    in.reset();
                    return false;
                } else {

                    System.out.println("package =" + in.toString());
                    byte[] bytes = new byte[length - 4];
                    in.get(bytes, 0, length - 4);
                    String str = new String(bytes,"UTF-8");

                    JSONObject jsonObject = new JSONObject(str);

                    try {
                        int messageType = jsonObject.getInt(TrackMsg.KEY_MSG_TYPE);
                        JSONObject trackInfo = jsonObject.getJSONObject(TrackMsg.KEY_TRACK_INFO);
                        byte[] trackPngBase64 = jsonObject.getString(TrackMsg.KEY_TRACK_PNG).getBytes(Charset.forName("UTF-8"));
                        byte[] trackPng = Base64.decodeBase64(trackPngBase64);
                        TrackMsg trackMsg = new TrackMsg(messageType, trackInfo, trackPng);
                        out.write(trackMsg);

                    } catch (JSONException ex) {
                    	System.out.println("parsing json msg failed");
                        System.out.println("string = " + str);
                    }

                    if(in.remaining() > 0){
                    	return true;
                    }
                }
            }
            return false;
        }
}
