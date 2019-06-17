package simpleNetwork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class SendRecieve implements ISendRecieve
{
	@Override
	public void Send(DataOutputStream dos)
	{
		Scanner scn = new Scanner(System.in);
		
		while (true) { 
             
			// read the message to deliver. 
            String msg = scn.nextLine();
            try { 
                // write on the output stream 
                dos.writeUTF(msg); 
            } catch (IOException e) { 
                e.printStackTrace(); 
            } 
        } 
		
	}
	
	@Override
	public void Recieve(DataInputStream dis)
	{
		while (true) { 
            try { 
                String msg = dis.readUTF(); 
                System.out.println(msg); 
                
            } catch (IOException e) { 

                e.printStackTrace(); 
            } 
        } 
		
	}
	

}
