package communication.link;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import carddeckplatform.game.gameEnvironment.GameEnvironment;
import carddeckplatform.game.gameEnvironment.GameEnvironment.ConnectionType;

import communication.messages.Message;


public class ServerConnection implements Runnable{
	private Socket socket;
	//private Client client;
	private Sender sender;
	private Receiver receiver;
	private CountDownLatch cdl;
	private  LinkedBlockingQueue<ActionForQueue> commandsQueue;
	private volatile boolean stopped;
	private Connector connector;
	
	
	//---Singleton implementation---//
		private static class ServerConnectionHolder
		{
			private final static ServerConnection serverConnection=new ServerConnection();
		}
		/**
		 * get server connection instance
		 */
		public static ServerConnection getConnection(){
			return ServerConnectionHolder.serverConnection;
		}

		private abstract class ActionForQueue{			
			public abstract void execute();
		}
		private class SendMessageExecutor extends ActionForQueue{
			private Message msg;
			public SendMessageExecutor(Message msg) {
				this.msg=msg;
			}
			@Override
			public void execute() {
				if (sender!=null){
					sender.send(msg);
				}
				
			}
		}
		private class OpenConnectionExecutor extends ActionForQueue{

			@Override
			public void execute() {
				// uses TCP if specified or if the current player is the hosting player.
				if(GameEnvironment.get().getConnectionType()==ConnectionType.TCP || GameEnvironment.get().getPlayerInfo().isServer())
					connector = new TcpConnector(GameEnvironment.get().getTcpInfo().getHostIp(),GameEnvironment.get().getTcpInfo().getHostPort());
				else if(GameEnvironment.get().getConnectionType()==ConnectionType.BLUETOOTH)
					connector = new BlueToothConnector(GameEnvironment.get().getBluetoothInfo().getHostDevice());
				Streams stream = connector.connect();
				if (stream!=null && stream.getIn()!=null && stream.getOut()!=null){
					ObjectOutputStream out = stream.getOut();
					ObjectInputStream in = stream.getIn();
				
					sender = new Sender(out);
					receiver = new Receiver(in);
					receiver.initializeMode();
					sender.initializeMode();
					//GameEnvironment.getGameEnvironment().getHandler().post(receiver);
					new Thread(receiver).start();
				
				}
				cdl.countDown();
			}		
				
		
		}
			
		
	private class CloseConnectionExecutor extends ActionForQueue{

		@Override
		public void execute() {
			receiver.stop();
			connector.disconnect();			
//			try {	
//				if (socket!=null){
//					sender.closeStream();
//					receiver.closeStream();
//					socket.close();
//				}
//			
//			} catch (IOException e) {		
//				e.printStackTrace();
//			}
			
		}
		
	}
		
	
		
	
	
	private ServerConnection(){
		commandsQueue=new LinkedBlockingQueue<ActionForQueue>();
		this.stopped=false;
		
		new Thread(this).start();
	}
	//---Public methods---//
	
	public void openConnection() throws IOException{
		 cdl = new CountDownLatch(1);
		commandsQueue.add(new OpenConnectionExecutor());
		try {
			cdl.await();
			if (sender==null || receiver==null){
				throw new IOException("Host is unreachable");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while (!stopped){
			try {
				commandsQueue.take().execute();
			} catch (InterruptedException e) {
			}
		}		
	}
	
	public void closeConnection(){	
		commandsQueue.add(new CloseConnectionExecutor());
	}
	
	public void shutDown(){
		this.stopped=true;
		//add a new blank command to wake controller and stop it
		commandsQueue.add(new ActionForQueue() {				
			@Override
			public void execute() {	}
		});			
	}
	

	public void send(Message msg){
		commandsQueue.add(new SendMessageExecutor(msg));
	}
}
