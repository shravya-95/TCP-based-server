import java.net.*;
//import java.util.*;
//import java.util.concurrent.Semaphore;
import java.io.*;
import java.util.Hashtable;
import java.util.concurrent.Semaphore;


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





//  public void run () {
//    try {
//      while (true){
//        InputStream in  = s.getInputStream();
//        ObjectInputStream oin = new ObjectInputStream( in );
//        Request request = (Request) oin.readObject();
//        System.out.println(request.getRequestType());
//      }
//      InputStream istream = s.getInputStream ();
//      OutputStream ostream = s.getOutputStream ();
//      byte buffer[] = new byte[512];
//      int count;
//
//      //Might need to use objectinputstream like get object
//      while ((count = istream.read(buffer)) >= 0) {
////        Request
//
//        // String outMsg = msg.toUpperCase( );//change to what we need to send to output buffer
//
//        String outMsg="";
//        byte[] outBuf = outMsg.getBytes();
//
//        ostream.write(outBuf, 0, outBuf.length);
//        ostream.flush();
//
//        System.out.write(buffer, 0, count);
//        System.out.flush();
//      }
//      System.out.println("Client exit.");
//      s.close();
//    } catch (IOException ex) {
//      ex.printStackTrace ();
//    } finally {
//      try {
//        s.close ();
//      } catch (IOException ex) {
//        ex.printStackTrace ();
//      }
//    }
//  }

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

      InputStream in  = client.getInputStream();
      ObjectInputStream oin = new ObjectInputStream( in );
      try {
        Request request = (Request) oin.readObject();
        System.out.println("Read");
        System.out.println(request.getRequestType());
      }
      catch ( ClassNotFoundException e) {
        e.printStackTrace();
      }
      TCPServer c = new TCPServer (client);
      c.start ();
    }
  }
}
