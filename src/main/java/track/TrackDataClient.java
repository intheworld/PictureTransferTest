package track;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.apache.mina.util.Base64;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/**
 * Created by lshw on 17/3/13.
 */

public class TrackDataClient {
    public static final String TAG = TrackDataClient.class.getSimpleName();
    
    public static final int PORT = 15800;

    private InetAddress mAddr;

    private int port;

    private static final long CONNECT_TIMEOUT = 10 * 1000L;

    TrackDataClient(InetAddress addr, int port) {
        mAddr = addr;
        this.port = port;
    }
    
    public static void main(String[] args) {
    	try {
			TrackDataClient client = new TrackDataClient(InetAddress.getByName("localhost"), PORT);
			client.run();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void run() throws Throwable {
        NioSocketConnector connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);

        connector.getFilterChain().addLast("codec",
                    new ProtocolCodecFilter(new TrackMessageCodecFactory()));

        connector.getFilterChain().addLast("logger", new LoggingFilter());
        connector.setHandler(new IoHandlerAdapter() {
            @Override
            public void messageReceived(IoSession session, Object message) throws Exception {
                super.messageReceived(session, message);
                //TrackMsg msg = (TrackMsg)message;
            }
            
            public void sessionOpened(IoSession session) throws Exception {
            }
        });
        IoSession session;

        for (;;) {
            try {
                ConnectFuture future = connector.connect(new InetSocketAddress(mAddr, port));
                future.awaitUninterruptibly();
                session = future.getSession();
            	TrackMsg message = constructTrackMsg();
                session.write(message);
                break;
            } catch (RuntimeIoException e) {
                System.err.println("Failed to connect. ");
                e.printStackTrace();
                Thread.sleep(100 * 1000);
            }
        }
        // wait until the update is done!
        session.getCloseFuture().awaitUninterruptibly();
        connector.dispose();
    }
    public static TrackMsg constructTrackMsg() {
    	int msgType = 0;
    	JSONObject trackInfo = new JSONObject();
    	trackInfo.put("trackID", 5);
    	byte[] png = new byte[1024];
		try {
			png = readFileByBytes("D:\\cat.png");
			writeFileByBytes("D:\\client\\pngInBase64.txt", Base64.encodeBase64(png));
			writeFileByBytes("D:\\client\\client.png", png);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return new TrackMsg(msgType, trackInfo, png);
    }
    
    public static byte[] readFileByBytes(String fileName) throws IOException {
    	File file=new File(fileName);
    	if(!file.exists()||file.isDirectory()) {
    		throw new FileNotFoundException();
    	}
    	FileInputStream fis = new FileInputStream(file);
    	byte[] buf = new byte[1024];
    	IoBuffer byteBuffer = IoBuffer.allocate(1000 * 1000).setAutoExpand(true);
    	int length;
		while((length = fis.read(buf))!=-1) {
    		byteBuffer.put(buf, 0, length);
    	}
		byteBuffer.flip();
		int remaining = byteBuffer.remaining();
		byte[] bytes = new byte[remaining];
		byteBuffer.get(bytes, 0, remaining);
    	return bytes;
    }
    
	public static void writeFileByBytes(String path, byte[] bytes) throws IOException {
		File file = new File(path);
		if(!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream out = new FileOutputStream(file, true);
		out.write(bytes);
		out.close();
	}
}
