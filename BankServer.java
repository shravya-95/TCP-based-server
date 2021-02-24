import java.net.*;
//import java.util.*;
//import java.util.concurrent.Semaphore;
import java.io.*;
import java.util.Hashtable;

class Account{
  public int uid;// unique Id for accounts:: use an integer sequence counter starting with 1
  int balance = 0;
  public Account(int uid){
    this.uid=uid;
  }
  public int withdraw(int amount) {

    this.balance = this.balance - amount;
    return this.balance;

  }
  public int deposit(int amount){
    this.balance = this.balance+amount;
    return this.balance;
  }
  public int getBalance(){
    return this.balance;
  }
}

public class BankServer extends Thread {
  protected Socket s;
  protected static Hashtable<Integer, Account> accounts;
  BankServer (Socket s) {
    System.out.println ("New client.");
    this.s = s;
  }
  public synchronized void transfer(int target, int source, int amount) throws InterruptedException {
    if(accounts.get(source).getBalance()<amount){
      //write to log file
      return;
    }
    accounts.get(source).withdraw(amount);
    accounts.get(target).deposit(amount);
    String msg = "Transferred %d from %d to %d";
    System.out.printf(msg,amount,source,target);
    notifyAll();
  }

  public void run ()  {
//    while (true){
    try {
      OutputStream out = s.getOutputStream();
      ObjectOutputStream outstream = new ObjectOutputStream(out);
      InputStream instream = s.getInputStream();
      ObjectInputStream oinstream = new ObjectInputStream(instream);
      Request request = (Request) oinstream.readObject();
      String requestType = request.getRequestType();
      System.out.println("Request type:" + requestType);
      switch (requestType) {
        case "createAccount": {
          int uid = ((CreateAccountRequest) request).getNewUid();
          Account account = new Account(uid);
          accounts.put(uid, account);
          System.out.println("created account");
          Response createResponse = new CreateAccountResponse(uid);
          System.out.println("created response in server");
          outstream.writeObject(createResponse);
          System.out.println("wrote response in server");
//          outstream.flush();
          break;
        }
        case "deposit": {
          System.out.println("in deposit");
          DepositRequest depositRequest = (DepositRequest) request;
          int uid = depositRequest.getUid();
          Account account = accounts.get(uid);
          System.out.printf("before: %d", accounts.get(uid).getBalance());
          account.deposit(100); //check if this updates or need to put again
          System.out.printf("After: %d", accounts.get(uid).getBalance());
          Response createResponse = new DepositResponse(true);
          outstream.writeObject(createResponse);

          break;
        }
        case "getBalance": {
          System.out.println("in getBalance");
          GetBalanceRequest getBalanceRequest = (GetBalanceRequest) request;
          int uid = getBalanceRequest.getUid();
          Account account = accounts.get(uid);
          if (account==null){
            System.out.printf("Account uid %d not found",uid);
            break;
          }
          System.out.printf("get balance reqest processed for uid %d",uid);
          Response getBalanceResponse = new GetBalanceResponse(account.getBalance());
          System.out.printf("get balance response sent for uid %d , balance is ",uid);
          outstream.writeObject(getBalanceResponse);
          break;
        }
        case "transfer": {
          System.out.println("in transfer");
          TransferRequest transferRequest = (TransferRequest) request;
          int sourceUid = transferRequest.getSourceUid();
          int targetUid = transferRequest.getTargetUid();
          int amount = transferRequest.getAmount();
          try {
            this.transfer(targetUid, sourceUid, amount);
          } catch (InterruptedException ex) {
            ex.printStackTrace();
          }
          break;
        }
        default:
          throw new RuntimeException("Illegal request type");
      }
      System.out.println("Client exit.");
//        TODO: check if have to close socket
//      s.close();
    } catch (IOException ex) {
//      ;
      ex.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
//      ;
    }
//    TODO: this was giving IOException - commented for now. but with new socket for each request, this works fine.
      finally {
      try {
        System.out.println("closing socket");
        s.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }
//  }

  public static void main (String args[]) throws IOException {

    accounts = new Hashtable<>();
    if (args.length != 1)
         throw new RuntimeException ("Syntax: TCPClient serverPortnumber");

    System.out.println ("Starting on port " + args[0]);
    ServerSocket server = new ServerSocket (Integer.parseInt (args[0]));

    while (true) {
      System.out.println ("........Waiting for a client request");
      Socket client = server.accept ();
      System.out.println( "Received request from " + client.getInetAddress ());
      System.out.println( "Starting worker thread..." );
      BankServer bankServer = new BankServer(client);
      bankServer.start();
//      client.close();
    }

  }
}
