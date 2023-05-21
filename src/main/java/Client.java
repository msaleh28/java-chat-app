import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;



public class Client extends Thread{

	
	Socket socketClient;
	
	ObjectOutputStream out;
	ObjectInputStream in;
	ChatInfo datapack = new ChatInfo();
	
	private Consumer<Serializable> callback;
	
	Client(Consumer<Serializable> call){
	
		callback = call;
	}
	
	public void run() {
		
		try {
		socketClient= new Socket("127.0.0.1",5555);
	    out = new ObjectOutputStream(socketClient.getOutputStream());
	    in = new ObjectInputStream(socketClient.getInputStream());
	    socketClient.setTcpNoDelay(true);
		}
		catch(Exception e) {}
		
		while(true) {
			 
			try {
			datapack = (ChatInfo) in.readObject();
			System.out.println("client callback");
			callback.accept(datapack);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	
    }
	
	public void send(ChatInfo data) {
		
		try {
			out.writeObject(data);
			out.reset();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
