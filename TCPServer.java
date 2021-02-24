import java.net.*;
//import java.util.*;
import java.util.concurrent.Semaphore;
import java.io.*;
import java.util.Hashtable;


class Account{
     int UID;// unique Id for accounts:: use an integer sequence counter starting with 1
     int balance;
     Semaphore available;
 }

public class TCPServer extends Thread {
  protected Socket s;
  protected static Hashtable<Integer, Account> accounts;
  TCPServer (Socket s) {
    System.out.println ("New client.");
    this.s = s;
  }

  public void run () {
    try {
      InputStream istream = s.getInputStream ();
      ObjectInputStream oinstream = new ObjectInputStream (istream);
      //TODO: check if we need the below while loop
//      while (oinstream.available() >= 0) {
        Request request = (Request) oinstream.readObject();
        System.out.println("Read");
        System.out.println(request.getRequestType());
        System.out.println("After Read");
//      }
      System.out.println("Client exit.");
      s.close();
    } catch (IOException ex) {
      ex.printStackTrace ();
    } catch ( ClassNotFoundException e) {
        e.printStackTrace();
      } finally {
      try {
        s.close ();
      } catch (IOException ex) {
        ex.printStackTrace ();
      }
    }
  }

  public static void main (String args[]) throws IOException {

    accounts = new Hashtable<>();
    if (args.length != 1)
         throw new RuntimeException ("Syntax: EchoServer port-number");

    System.out.println ("Starting on port " + args[0]);
    ServerSocket server = new ServerSocket (Integer.parseInt (args[0]));

    while (true) {
      System.out.println ("Waiting for a client request");
      Socket client = server.accept ();
      System.out.println( "Received request from " + client.getInetAddress ());
      System.out.println( "Starting worker thread..." );
      TCPServer tcpServer = new TCPServer(client);
      tcpServer.start();
    }
  }
}
