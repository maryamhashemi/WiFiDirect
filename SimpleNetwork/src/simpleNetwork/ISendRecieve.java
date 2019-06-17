package simpleNetwork;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public interface ISendRecieve {
	
	public void Send(DataOutputStream dos);
	public void Recieve(DataInputStream dis);

}
