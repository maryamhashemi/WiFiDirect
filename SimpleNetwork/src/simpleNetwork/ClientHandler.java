package simpleNetwork;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

// ClientHandler class 
class ClientHandler implements IClientHandler  
{ 
    private String name; 
    DataInputStream dis; 
    DataOutputStream dos; 
    Socket s; 
    boolean isloggedin; 
      
    // constructor 
    public ClientHandler(Socket s, String name, 
                            DataInputStream dis, DataOutputStream dos) { 
        this.dis = dis; 
        this.dos = dos; 
        this.name = name; 
        this.s = s; 
        this.isloggedin=true; 
    } 
  
    @Override
    public void Broadcast()
    {   
        String received; 
        while (true)  
        { 
            try
            { 
                // A server recieved  a message from one of the clients
                received = dis.readUTF();                  
                System.out.println(this.name + ": "+ received);
                
                  
                if(received.equals("logout")){ 
                    this.isloggedin=false; 
                    this.s.close(); 
                    break; 
                } 
                  
                // Broadcast message to all logged in clients
                for (ClientHandler c : Server.clients)  
                { 
                    if (c.name.equals(this.name) && c.isloggedin == true)  
                    { 
                    	c.dos.writeUTF("You : "+ received);        
                    } 
                    else if (c.isloggedin == true)
                    {
                    	c.dos.writeUTF(this.name + ": "+ received);
                    }
                    
                } 
                if (Server.C != null)
                	Server.C.Send(received);;
                
            } catch (IOException e) { 
                  
                e.printStackTrace(); 
            } 
              
        } 
        
        try
        { 
            // closing resources 
            this.dis.close(); 
            this.dos.close(); 
              
        }catch(IOException e){ 
            e.printStackTrace(); 
        } 
    } 
} 
