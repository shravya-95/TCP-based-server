import java.net.*;
import java.io.*;
import java.util.Hashtable;

  /**
   * Account Class
   * attributes: uid and account
   * methods: withdraw, deposit, getBalance
   */
class Account{
  public int uid;// unique Id for accounts - an integer sequence counter starting with 1
  int balance = 0;

  public Account(int uid){
    this.uid=uid;
  }

  /**
   * Withdraws given amount from account by subtracting from balance
   * @param amount Amount to be withdrawn
   * @return new balance
   */
  public int withdraw(int amount) {
    this.balance = this.balance - amount;
    return this.balance;
  }

  /**
   * Deposit given amount to account by adding to balance
   * @param amount Amount to be deposited
   * @return new balance
   */
  public int deposit(int amount){
    this.balance = this.balance+amount;
    return this.balance;
  }

  /**
   * Getter method to access balance
   * @return balance
   */
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

  /**
   * Transfer method to transfer balance from source account to target account. This method is synchronized to access critical sections.
   * @parameters target(uid of target account), source(uid of source account) and amount(to be transferred)
   * @return status(true for successful transfer, false for unsuccessful) 
   */
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

  /**
   * writeToLog method to log. This method is synchronized to access critical sections.
   * @parameters fileName, content
   */
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
  /**
   * run invoked whenever thread starts
   */
  public void run (){
    String logMsg = "";
    String[] content = new String[3];

    try {
      OutputStream out = s.getOutputStream();
      ObjectOutputStream outstream = new ObjectOutputStream(out);
      InputStream instream = s.getInputStream();
      ObjectInputStream oinstream = new ObjectInputStream(instream);
      
      //reading the request object sent by client
      Request request = (Request) oinstream.readObject();
      String requestType = request.getRequestType();
      System.out.println("Request type: " + requestType);

      //based on the request type, the request is handles and response is sent
      switch (requestType) {
        case "createAccount": {
          //create an account object and add it to the accounts hashtable.
          int uid = ((CreateAccountRequest) request).getNewUid();
          Account account = new Account(uid);
          accounts.put(uid, account);

          //return the uid of the new account in response
          Response createResponse = new CreateAccountResponse(uid);
          outstream.writeObject(createResponse);

          //logging
          content[0]="createAccount";
          content[1]="";
          content[2]= String.valueOf(uid);
          break;
        }
        case "deposit": {
          //read the request and deposit the amount to the account mentioned in the request
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

          //status of deposit is sent in response
          Response createResponse = new DepositResponse(status);
          outstream.writeObject(createResponse);

          //logging
          content[0]="deposit";
          content[1]= "UID: "+uid + "," + "Amount:" + depositRequest.getAmount();
          content[2]= String.valueOf(((DepositResponse) createResponse).getStatus());
          break;
        }
        case "getBalance": {
          //read the request and check the balance of the account mentioned in the request
          GetBalanceRequest getBalanceRequest = (GetBalanceRequest) request;
          int uid = getBalanceRequest.getUid();
          Account account = accounts.get(uid);
          if (account==null){
            System.out.printf("Account uid %d not found",uid);
            break;
          }

          //current balance is sent in response
          Response getBalanceResponse = new GetBalanceResponse(account.getBalance());
          outstream.writeObject(getBalanceResponse);

          //logging
          content[0]="getBalance";
          content[1]="UID: "+uid;
          content[2]= String.valueOf(((GetBalanceResponse) getBalanceResponse).getBalance());
          break;
        }
        case "transfer": {
          //read the request and transfer the amount between the accounts mentioned in the request
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

          //status of transfer is sent in response
          Response transferResponse = new TransferResponse(status);
          outstream.writeObject(transferResponse);

          //logging
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
    //hashtable to hold the account's uid and object
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
