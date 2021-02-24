import java.net.*;
//import java.util.*;
import java.util.concurrent.Semaphore;
import java.io.*;
import java.util.Hashtable;


class Account{
     int uid;// unique Id for accounts:: use an integer sequence counter starting with 1
     int balance;
     Semaphore available;
     Account(int uid){
       this.uid=uid;
       this.balance=0;
       this.available = new Semaphore(1);
     }
 }

public class BankServer extends Thread {
  protected Socket s;
  protected static Hashtable<Integer, Account> accounts;
  BankServer (Socket s) {
    System.out.println ("New client.");
    this.s = s;
  }

  public void run () {
    try {
      InputStream istream = s.getInputStream ();
      ObjectInputStream oinstream = new ObjectInputStream (istream);
      OutputStream out = s.getOutputStream();
      ObjectOutputStream os = new ObjectOutputStream(out);
      //TODO: check if we need the below while loop
//    while (oinstream.available() >= 0) {
      Request request = (Request) oinstream.readObject();
      System.out.println("Read");
      System.out.println(request.getRequestType());
      System.out.println("After Read");
      String requestType = request.getRequestType();
      //handling createAccountRequest
      switch(requestType){
        case "createAccount": {
          int uid = ((CreateAccountRequest)request).getNewUid();
          Account account = new Account(uid);
          accounts.put(uid,account);
          Response createResponse = new CreateAccountResponse(uid);
          os.writeObject(createResponse);
        }
        case "deposit": {
          DepositRequest request = (DepositRequest) request;
          int uid = request.getUid();
          Account account = accounts.get(uid);
          //need to do synchonise here?
          account.balance += 100; //check if this updates or need to put again
          Response createResponse = new DepositResponse(true);
          os.writeObject(createResponse);
        }
      }


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
         throw new RuntimeException ("Syntax: TCPClient serverPortnumber");

    System.out.println ("Starting on port " + args[0]);
    ServerSocket server = new ServerSocket (Integer.parseInt (args[0]));

    while (true) {
      System.out.println ("Waiting for a client request");
      Socket client = server.accept ();
      System.out.println( "Received request from " + client.getInetAddress ());
      System.out.println( "Starting worker thread..." );
      BankServer bankServer = new BankServer(client);
      bankServer.start();
    }
  }
}
