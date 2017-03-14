package track;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * Created by lshw on 17/3/13.
 */

public class TrackMessageCodecFactory implements ProtocolCodecFactory {
    private TrackMessageEncoder encoder;
    private TrackMessageDecoder decoder;

    public TrackMessageCodecFactory() {
        encoder = new TrackMessageEncoder();
        decoder = new TrackMessageDecoder();
    }

    public ProtocolDecoder getDecoder(IoSession session) throws Exception {
        return decoder;
    }

    public ProtocolEncoder getEncoder(IoSession session) throws Exception {
        return encoder;
    }
}