package simpleNetwork;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("please enter s for Server and c for Client:");
        Scanner scn = new Scanner(System.in);
        String line = scn.nextLine();
        String[] token = line.split(" ");

        if (token[0].equals("c")) {
            Client c = new Client();

            int port = Integer.parseInt(token[1]);

            c.Connect(port);
            c.Receive();

            while (true) {
                // read the message to deliver.
                String msg = scn.nextLine();
                c.Send(msg);
            }


        } else if (token[0].equals("s")) {
            int port = Integer.parseInt(token[1]);
            Server s = new Server();
            s.Send();
            s.Accept(port);

        }
    }
}
