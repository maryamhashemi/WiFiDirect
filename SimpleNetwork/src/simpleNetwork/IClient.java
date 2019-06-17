package simpleNetwork;

public interface IClient {
	
	public void Connect(int port);
	public void Send(String msg);
	public void Recieve();

}
