package track;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;	
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class TrackDataServer {
	    private static final int PORT = TrackDataClient.PORT;
	    public static void main( String[] args ) throws IOException
	    {
	        IoAcceptor acceptor = new NioSocketAcceptor();
	        acceptor.getFilterChain().addLast( "logger", new LoggingFilter() );
	        acceptor.getFilterChain().addLast( "codec", new ProtocolCodecFilter(new TrackMessageCodecFactory()));
	        acceptor.setHandler(new IoHandlerAdapter() {
	            @Override
	            public void exceptionCaught( IoSession session, Throwable cause ) throws Exception
	            {
	                cause.printStackTrace();
	            }
	            @Override
	            public void messageReceived( IoSession session, Object message ) throws Exception
	            {
	                TrackMsg msg = (TrackMsg) message;
	                writeFileByBytes("D:\\server\\cat.png", msg.trackPng);
	                System.out.println("Message written...");
	            }
	            @Override
	            public void sessionIdle( IoSession session, IdleStatus status ) throws Exception
	            {
	                //System.out.println( "IDLE " + session.getIdleCount( status ));
	            }
	        });
	        acceptor.getSessionConfig().setReadBufferSize( 20 * 1024 );
	        acceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE, 10 );
	        acceptor.bind( new InetSocketAddress(PORT) );
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
