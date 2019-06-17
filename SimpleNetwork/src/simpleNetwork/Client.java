package simpleNetwork;
import java.io.*; 
import java.net.*;
import java.util.Scanner; 
  
public class Client implements IClient
{ 
    final int ServerPort = 1234; 
    public Socket s;
    public DataInputStream dis;
    public DataOutputStream dos;
    
    
    SendRecieve sr;
    
    @Override
    public void Connect(int port) {  	
    	
    	InetAddress ip = null;
		try {
			// getting localhost ip 
	        ip = InetAddress.getByName("localhost");
	        
		} catch (UnknownHostException e) {

			e.printStackTrace();
		}   
		
		try {
			// establish the connection 
	        s = new Socket(ip, port); 
	          
	        // obtaining input and out streams 
	        dis = new DataInputStream(s.getInputStream()); 
	        dos = new DataOutputStream(s.getOutputStream()); 
	        
		} catch (IOException e) {
			
			e.printStackTrace();
		} 
    }
        
  
    @Override
    public void Send(String msg) {   	

    	             
    	            try { 
    	                // write on the output stream 
    	                dos.writeUTF(msg); 
    	            } catch (IOException e) { 
    	                e.printStackTrace(); 
    	            } 

    }
    
    @Override
    public void Recieve() {  	
    	// readMessage thread 
        Thread RecieveMessage = new Thread(new Runnable() {
    		
    		@Override
    		public void run() {
    			//sr.Recieve(dis);
    			while (true) { 
    	            try { 
    	                String msg = dis.readUTF(); 
    	                for (ClientHandler c : Server.clients)  
    	                { 
    	                	c.dos.writeUTF(msg);    	    	                   
    	                }
    	                System.out.println(msg); 
    	                
    	            } catch (IOException e) { 

    	                e.printStackTrace(); 
    	            } 
    	        } 
    			
    		}
    	}); 
        
        RecieveMessage.start();
    }
} 