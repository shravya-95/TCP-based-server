import java.net.*;
//import java.util.*;
import java.util.concurrent.Semaphore;
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
  public int getTotalBalance(){
    int totalBalance=0;
    for (int i=0;i<accounts.size();i++){
      totalBalance+=accounts.get(i).getBalance();
    }
    if (totalBalance!=10000){
      System.out.printf("Total %d did not add up to 10,000",totalBalance);
    }
    return totalBalance;
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
          DepositRequest depositRequest = (DepositRequest) request;
          int uid = depositRequest.getUid();
          Account account = accounts.get(uid);
          //need to do synchonise here? - no because deposits are serialized
          account.deposit(100); //check if this updates or need to put again
          Response createResponse = new DepositResponse(true);
          os.writeObject(createResponse);
        }
        case "getBalance":{
          GetBalanceRequest getBalanceRequest = (GetBalanceRequest) request;
          int uid = getBalanceRequest.getUid();
          Account account = accounts.get(uid);
          Response getBalanceResponse = new GetBalanceResponse(account.getBalance());
          os.writeObject(getBalanceResponse);

        }
        case "transfer":{
          TransferRequest transferRequest = (TransferRequest) request;
          int sourceUid = transferRequest.sourceUid;
          int targetUid = transferRequest.targetUid;
          int amount = transferRequest.amount;
          try {
            this.transfer(targetUid, sourceUid, amount);
          }catch (InterruptedException ex){
            ex.printStackTrace();
          }
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
