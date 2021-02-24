import java.net.*;
import java.io.*;
import java.util.Random;

public class BankClient extends Thread{
    String host;
    int port;
    int[] uids;
    int iterationCount;
    BankClient(int[] uids, int iterationCount, String host, int port){
        this.host = host;
        this.port = port;
        this.uids = uids;
        this.iterationCount = iterationCount;
    }

    public void run(){
        try {
            for(int i=0;i<iterationCount;i++){
                Socket socket = new Socket(host, port);
                OutputStream out = socket.getOutputStream();
                ObjectOutputStream outstream = new ObjectOutputStream(out);
                InputStream instream = socket.getInputStream();
                ObjectInputStream oinstream = new ObjectInputStream(instream);
                int rnd1 = new Random().nextInt(uids.length);
                int rnd2 = new Random().nextInt(uids.length);

                Request transferRequest = new TransferRequest(uids[rnd1], uids[rnd2], 10);
                outstream.writeObject(transferRequest);

                TransferResponse transferResponse = (TransferResponse) oinstream.readObject();

//                System.out.print("transfer status");
//                System.out.println(transferResponse.getStatus());
                if(!transferResponse.getStatus()){
                    //write to log file
                }
            }
        } catch (IOException e){
            e.printStackTrace ();
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }


    }
    public static void main (String args[]){
        if ( args.length != 4 ) {
            throw new RuntimeException( "Syntax: java BankClient serverHostname severPortnumber threadCount iterationCount" );
        }
        String serverHostname = args[0];
        int serverPortnumber = Integer.parseInt( args[1] );
        int threadCount = Integer.parseInt( args[2] );
        int iterationCount = Integer.parseInt( args[3] );
        System.out.println ("Connecting to " + serverHostname + ":" + serverPortnumber + "..");
        //TODO: change numAccounts to 100
        int numAccounts = 5;

        //1: sequentially create 100 threads
        int [] uids = createAccounts(numAccounts, serverHostname, serverPortnumber);
        //2: sequentially deposit 100 in each of these accounts
        deposit(uids, 100, numAccounts, serverHostname, serverPortnumber);
        //3: get balance. return value for this should be 10,000
        int balanace = getTotalBalance(numAccounts, uids, serverHostname, serverPortnumber);
//        System.out.printf("In main balanace: %d \n", balanace);

        //5: using join to wait for all the threads
        transfer(uids, threadCount, iterationCount, serverHostname, serverPortnumber);

        //6: get balance. return value should be 10,000
//        int balanace = getTotalBalance(numAccounts, uids, serverHostname, serverPortnumber);
//        System.out.printf("In main balanace: %d \n", balanace);
    }

    private static int[] createAccounts(int numAccounts, String serverHostname,int serverPortnumber) {
        int[] uids = new int[numAccounts];
        try {
            Socket socket;
            ObjectOutputStream os;
            ObjectInputStream is;
            OutputStream out;
            InputStream in;
            for (int i = 0; i < numAccounts; i++) {
                socket = new Socket (serverHostname, serverPortnumber);
                in = socket.getInputStream();
                out = socket.getOutputStream();
                os = new ObjectOutputStream(out);
                is = new ObjectInputStream (in);

                Request createRequest = new CreateAccountRequest();
                os.writeObject(createRequest);

                CreateAccountResponse createResponse = (CreateAccountResponse) is.readObject();
                uids[i] = createResponse.getUid();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return uids;
    }

    private static void deposit(int[] uids, int amount, int numAccounts, String serverHostname,int serverPortnumber) {
        try {
            Socket socket;
            ObjectOutputStream os;
            ObjectInputStream is;
            OutputStream out;
            InputStream in;
            for (int i = 0; i < numAccounts; i++) {
                socket = new Socket (serverHostname, serverPortnumber);
                in = socket.getInputStream();
                out = socket.getOutputStream();
                os = new ObjectOutputStream(out);
                is = new ObjectInputStream (in);
                Request depositRequest = new DepositRequest(uids[i],100);
                os.writeObject(depositRequest);
                DepositResponse depositResponse = (DepositResponse) is.readObject();
            }
        }catch (IOException e){
            e.printStackTrace ();
        } catch(ClassNotFoundException e){
            e.printStackTrace();
        }
    }
    private static void transfer(int[] uids, int threadCount, int iterationCount, String host, int port){
        for(int i=0;i<threadCount;i++){
            BankClient bankClient = new BankClient(uids, iterationCount, host, port);
            bankClient.start();
        }
    }


    public static synchronized void writeToLog(String sFileName, String sContent){
        try {

            File oFile = new File(sFileName);
            if (!oFile.exists()) {
                oFile.createNewFile();
            }
            if (oFile.canWrite()) {
                BufferedWriter oWriter = new BufferedWriter(new FileWriter(sFileName, true));
                oWriter.write (sContent);
                oWriter.close();
            }

        }
        catch (IOException oException) {
            throw new IllegalArgumentException("Error appending/File cannot be written: \n" + sFileName);
        }

    }

    public static int getTotalBalance(int numAccounts, int[] uids, String serverHostname,int serverPortnumber){
        int total = 0;
        try {
            Socket socket;
            ObjectOutputStream os;
            ObjectInputStream is;
            OutputStream out;
            InputStream in;
            for (int i = 0; i < numAccounts; i++) {
                socket = new Socket (serverHostname, serverPortnumber);
                in = socket.getInputStream();
                out = socket.getOutputStream();
                os = new ObjectOutputStream(out);
                is = new ObjectInputStream (in);
                Request getBalanceRequest = new GetBalanceRequest(uids[i]);
                os.writeObject(getBalanceRequest);
                GetBalanceResponse getBalanceResponse = (GetBalanceResponse) is.readObject();
                total += getBalanceResponse.getBalance();
                System.out.println(total);
            }
        }catch (IOException e){
            e.printStackTrace ();
        } catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        return total;
    }
}

