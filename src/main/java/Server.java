import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;
/*
 * Clicker: A: I really get it    B: No idea what you are talking about
 * C: kind of following
 */

public class Server{

	int count = 1;	
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	TheServer server;
	private Consumer<Serializable> callback;
	ArrayList<String> onServer = new ArrayList<String>();
	ChatInfo datapack = new ChatInfo();
	
	
	Server(Consumer<Serializable> call){
	
		callback = call;
		server = new TheServer();
		server.start();
	}
	
	
	public class TheServer extends Thread{
		
		public void run() {
		
			try(ServerSocket mysocket = new ServerSocket(5555);){
		    System.out.println("Server is waiting for a client!");
			
		    while(true) {
		
				ClientThread c = new ClientThread(mysocket.accept(), count);
				callback.accept("client has connected to server: " + "client #" + count);
				onServer.add("client " + count);
//				callback.accept("client " + count);
				clients.add(c);
//				c.sendUsersArray();
				c.start();
				count++;
//				c.sendUsersArray();
				
//				count++;
				
			    }
			}//end of try
				catch(Exception e) {
					e.printStackTrace();
					callback.accept("Server socket did not launch");
				}
			}//end of while
		}
	

		class ClientThread extends Thread{
			
		
			Socket connection;
			int count;
			ObjectInputStream in;
			ObjectOutputStream out;
			
			ClientThread(Socket s, int count){
				this.connection = s;
				this.count = count;	
			}
			
			public synchronized void specifiedMessage(String message) {
				for(int i : datapack.specifiedIndices) {
	    			int index = i-3; // client list starts at index 3
	    			ClientThread t = clients.get(index);
					try {
					 datapack.message = "PRIVATE MSG client "+count+" said: " + message;
					 datapack.currentUsers = null;
					 t.out.writeObject(datapack);
					 t.out.reset();
					}
					catch(Exception e) {}
	    			
	    			}
			}
			
			public synchronized void updateClients(String message) {
				for(int i = 0; i < clients.size(); i++) {
					ClientThread t = clients.get(i);
					try {
					 datapack.message = message;
					 datapack.currentUsers = null;
					 t.out.writeObject(datapack);
					 t.out.reset();
					}
					catch(Exception e) {}
				}
			}
			
			public synchronized void sendUsersArray() {
				System.out.println(onServer);				
				datapack.currentUsers = onServer;
				System.out.println("send users array " + datapack.currentUsers);
				System.out.println("send users clients " + clients);
				try {
					for(int i = 0; i < clients.size(); i++) {
						ClientThread t = clients.get(i);
						// need to make message blank for the GuiServer runLater logic to work
						datapack.message = "";
						System.out.println("inside loop users " + datapack.currentUsers);
						System.out.println("inside loop message " + datapack.message);
						System.out.println("inside loop " + datapack);
						t.out.writeObject(datapack);
						// System.out.println("after writeobject");
						t.out.reset();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			public void run(){
				synchronized(this) {
					
				
				try {
					in = new ObjectInputStream(connection.getInputStream());
					out = new ObjectOutputStream(connection.getOutputStream());
					connection.setTcpNoDelay(true);	
					}
				catch(Exception e) {
					System.out.println("Streams not open");
					}
				
				datapack.currentUsers = null;
				updateClients("new client on server: client #"+count);
//				onServer.add("client " + count);
				//System.out.println(onServer);
				datapack.currentUsers = onServer;
				datapack.message = "";
				sendUsersArray();
					
				 while(true) {
					    try {
					    	datapack = (ChatInfo) in.readObject();
					    	if(datapack.specificFlag == false) {
					    		callback.accept("client: " + count + " sent: " + datapack.message);
					    		// currentUsers must be null for runLater logic to work
					    		datapack.currentUsers = null;
					    		updateClients("client #"+count+" said: "+datapack.message);
					    		// reinstate currentUsers then send client list
					    		datapack.currentUsers = onServer;
					    		datapack.message = "";
					    		sendUsersArray();
					    		}
					    	else { // for specified client(s) messages
					    		callback.accept("PRIVATE MSG client: " + count + " sent: " + datapack.message);
					    		datapack.currentUsers = null;
					    		specifiedMessage(datapack.message);
					    		datapack.currentUsers = onServer;
					    		datapack.message = "";
					    		datapack.specificFlag = false;
					    		sendUsersArray();
					    	
					    	}
					    }
					    catch(Exception e) {
					    	callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
					    	//updateClients("Client #"+count+" has left the server!");
					    	clients.remove(this);
					    	onServer.remove("client " + count);
					    	datapack.currentUsers = null;
					    	updateClients("Client #"+count+" has left the server!");
					    	datapack.currentUsers = onServer;
					    	datapack.message = "";
					    	sendUsersArray();
					    	// updateClients("Client #"+count+" has left the server!");
					    	//count--;
					    	break;
					    }
					}
			}//end of run
			
			
			}
			
		}//end of client thread
}


	
	

	
