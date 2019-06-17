package simpleNetwork;
import java.io.*; 
import java.util.*; 
import java.net.*; 

// Server class 
public class Server implements IServer
{ 
  
    // Vector to store active clients 
    public static Vector<ClientHandler> clients = new Vector<>(); 
      
    // Counter for clients 
    int i = 0; 
    
    // Server Port    
    int ServerPort = 1234;
    
    // Server is listening on port 1234 
    ServerSocket serverSocket;
    
    Socket socket;
  
    @Override
    public void Accept(int port)
    {
    	System.out.println("Server is running"); 
		try {
			serverSocket = new ServerSocket(port);
			
		    // running infinite loop for getting client request 
	        while (true)  
	        { 	        	
	            // Accept the incoming request 
	            socket = serverSocket.accept(); 
	  
	            System.out.println("New client request received : " + socket); 
	              
	            // obtain input and output streams 
	            DataInputStream dis = new DataInputStream(socket.getInputStream()); 
	            DataOutputStream dos = new DataOutputStream(socket.getOutputStream()); 
	              
	            System.out.println("Creating a new handler for this client..."); 
	  
	            // Create a new handler object for handling this request. 
	            ClientHandler client = new ClientHandler(socket,"client " + i, dis, dos); 
	  
	            // Create a new Thread with this object. 
	            Thread t = new Thread(new Runnable() {					
					@Override
					public void run() {
						client.Broadcast();
						
					}
				}); 
	              
	            System.out.println("Adding client " + i + " to active client list"); 
	  
	            // add this client to active clients list 
	            clients.add(client); 
	  
	            // start the thread. 
	            t.start();
	  
	            // increment i for new client. 
	            // i is used for naming only, and can be replaced 
	            // by any naming scheme 
	            i++; 
	  
	        } 
		} catch (IOException e) {
			
			e.printStackTrace();
		} 
         
    } 
    
    public static Client C = null;
    
    @Override
    public void Send()
    {
    	Thread SendMessage = new Thread( new Runnable() {
    		@Override
    		public void run() {
    			Scanner scn = new Scanner(System.in);

    			while (true) {  	             

    	            String msg = scn.nextLine();	
    	    		String[] token = msg.split(" ");
    	    		
    	            if(token[0].equals("c")) {
    	    	    	C = new Client();
    	    	    	int port = Integer.parseInt(token[1]); 
    	    	    	/*Thread client = new Thread(new Runnable() {		
    	    				@Override
    	    				public void run() {
    	    					
    	    		        	c.Connect();
    	    		        	c.Send();
    	    		        	c.Recieve();   
    	    					
    	    				}
    	    			});
    	    	    	client.start();*/
    	    	    	
    	    	    	C.Connect(port);
    	   
    	    	    	C.Recieve();
    	    	    	
    	            }
    	            else {
	    	            System.out.println("You: "+ msg);
	    	            
	    	            try { 
	    	            	for (ClientHandler c : clients)  
	    	                { 
	    	                    if (c.isloggedin == true)
	    	                    {
	    	                    	c.dos.writeUTF("Server: "+ msg);
	    	                    }
	    	                    
	    	                }
	    	            	
	    	            	if (C != null)
	    	            		C.Send(msg);
	    	                
	    	            } catch (IOException e) { 
	    	                e.printStackTrace(); 
	    	            }
    	            }
    	        }
    		}
    	});
        
        SendMessage.start();
    }
} 