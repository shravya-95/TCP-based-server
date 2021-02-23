import java.net.*;
import java.util.Hashtable;
import java.io.*;

class Account{
     int UID;// unique Id for accounts:: use an integer sequence counter starting with 1
     int balance;
 }

 //create hashtable

public class TCPServer extends Thread {
  public Hashtable<Integer,String> records = new Hashtable<>();
  protected Socket s;

  TCPServer (Socket s) {
    System.out.println ("New client.");
    this.s = s;
  }

  public void run () {
    try {
      InputStream istream = s.getInputStream ();
      OutputStream ostream = s.getOutputStream ();
      byte buffer[] = new byte[512];
      int count;

      //Might need to use objectinputstream like get object
      while ((count = istream.read(buffer)) >= 0) {
        String msg = new String( buffer );


        // String outMsg = msg.toUpperCase( );//change to what we need to send to output buffer


        byte[] outBuf = outMsg.getBytes();

        ostream.write(outBuf, 0, outBuf.length);
        ostream.flush();

        System.out.write(buffer, 0, count);
        System.out.flush();
      }
      System.out.println("Client exit.");
      s.close();
    } catch (IOException ex) {
      ex.printStackTrace ();
    } finally {
      try {
        s.close ();
      } catch (IOException ex) {
        ex.printStackTrace ();
      }
    }
  }

  public static void main (String args[]) throws IOException {

    if (args.length != 1)
         throw new RuntimeException ("Syntax: EchoServer port-number");

    System.out.println ("Starting on port " + args[0]);
    ServerSocket server = new ServerSocket (Integer.parseInt (args[0]));

    while (true) {
      System.out.println ("Waiting for a client request");
      Socket client = server.accept ();
      System.out.println( "Received request from " + client.getInetAddress ());
      System.out.println( "Starting worker thread..." );
      TCPServer c = new TCPServer (client);
      c.start ();
    }
  }
}
