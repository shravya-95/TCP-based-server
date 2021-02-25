import java.net.*;
import java.io.*;
import java.util.Hashtable;

class Account{
  public int uid;// unique Id for accounts - an integer sequence counter starting with 1
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
    this.s = s;
  }

  public synchronized boolean transfer(int target, int source, int amount) throws InterruptedException {
    if(accounts.get(source).getBalance()<amount){
      //write to log file
      return false;
    }
    accounts.get(source).withdraw(amount);
    accounts.get(target).deposit(amount);
    notifyAll();
    return true;
  }
  public static synchronized void writeToLog(String fileName, String content) throws IOException {
    try {

      File oFile = new File(fileName);
      if (!oFile.exists()) {
        oFile.createNewFile();
      }
      if (oFile.canWrite()) {
        BufferedWriter oWriter = new BufferedWriter(new FileWriter(fileName, true));
        oWriter.write(content);
        oWriter.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void run (){
    String logMsg = "";
    String[] content = new String[3];
    try {
      OutputStream out = s.getOutputStream();
      ObjectOutputStream outstream = new ObjectOutputStream(out);
      InputStream instream = s.getInputStream();
      ObjectInputStream oinstream = new ObjectInputStream(instream);
      Request request = (Request) oinstream.readObject();
      String requestType = request.getRequestType();
      System.out.println("Request type: " + requestType);
      switch (requestType) {
        case "createAccount": {
          int uid = ((CreateAccountRequest) request).getNewUid();
          Account account = new Account(uid);
          accounts.put(uid, account);
          Response createResponse = new CreateAccountResponse(uid);
          outstream.writeObject(createResponse);

          content[0]="createAccount";
          content[1]="";
          content[2]= String.valueOf(uid);
          break;
        }
        case "deposit": {
          DepositRequest depositRequest = (DepositRequest) request;
          int uid = depositRequest.getUid();
          Account account = accounts.get(uid);
          Boolean status = true;
          if (account==null){
            System.out.printf("Account uid %d not found",uid);
            status = false;
          }
          else{
            account.deposit(depositRequest.getAmount());
          }

          Response createResponse = new DepositResponse(status);
          outstream.writeObject(createResponse);
          content[0]="deposit";
          content[1]= "UID: "+uid + "," + "Amount:" + depositRequest.getAmount();
          content[2]= String.valueOf(((DepositResponse) createResponse).getStatus());
          break;
        }
        case "getBalance": {
          GetBalanceRequest getBalanceRequest = (GetBalanceRequest) request;
          int uid = getBalanceRequest.getUid();
          Account account = accounts.get(uid);
          if (account==null){
            System.out.printf("Account uid %d not found",uid);
            break;
          }
          Response getBalanceResponse = new GetBalanceResponse(account.getBalance());
          outstream.writeObject(getBalanceResponse);
          content[0]="getBalance";
          content[1]="UID: "+uid;
          content[2]= String.valueOf(((GetBalanceResponse) getBalanceResponse).getBalance());
          break;
        }
        case "transfer": {
          boolean status;
          TransferRequest transferRequest = (TransferRequest) request;
          int sourceUid = transferRequest.getSourceUid();
          int targetUid = transferRequest.getTargetUid();
          int amount = transferRequest.getAmount();
          try {
            status = this.transfer(targetUid, sourceUid, amount);
          } catch (InterruptedException ex) {
            status= false;
            ex.printStackTrace();
          }
          Response transferResponse = new TransferResponse(status);
          outstream.writeObject(transferResponse);
          content[0]="transfer";
          content[1]="From:"+ sourceUid +", To:"+ targetUid +", Amount:"+ amount;
          content[2]= String.valueOf(((TransferResponse) transferResponse).getStatus());
          break;
        }
        default:
          throw new RuntimeException("Illegal request type");
      }
      logMsg = String.format("Operation: %s | Inputs: %s | Result: %s \n", (Object[]) content);
      writeToLog("severLogfile.txt",logMsg);
    } catch (IOException ex) {
      ;
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
      finally {
      try {
        s.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }

  public static void main (String args[]) throws IOException {

    accounts = new Hashtable<>();
    if (args.length != 1)
         throw new RuntimeException ("Syntax: BankServer serverPortnumber");

    ServerSocket server = new ServerSocket (Integer.parseInt (args[0]));

    while (true) {
      System.out.println ("Waiting for a client request");
      Socket client = server.accept ();
      BankServer bankServer = new BankServer(client);
      bankServer.start();
    }
  }
}
