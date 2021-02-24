import java.net.*;
import java.io.*;
import java.util.Random;

public class BankClient extends Thread{
//    Request request;
//    String host, file;
//    int port;
//    Socket socket;
//    int iterationCount;
//    int[] uids;
    ObjectOutputStream os;
    ObjectInputStream is;
    int[] uids;
    int iterationCount;
    BankClient(ObjectOutputStream os,ObjectInputStream is, int[] uids,int iterationCount){
        System.out.println ("New client thread");
//        this.socket=socket;
        this.os = os;
        this.is = is;
        this.uids = uids;
        this.iterationCount = iterationCount;
    }


    public void run(){
        try {
            for(int i=0;i<iterationCount;i++){
                int rnd1 = new Random().nextInt(uids.length);
                int rnd2 = new Random().nextInt(uids.length);

                Request transferRequest = new TransferRequest(uids[rnd1], uids[rnd2], 10);
                os.writeObject(transferRequest);

                TransferResponse transferResponse = (TransferResponse) is.readObject();

                System.out.print("transfer status");
                System.out.println(transferResponse.getStatus());
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
        if ( args.length != 2 ) {
            throw new RuntimeException( "Syntax: java BankClient serverHostname severPortnumber threadCount iterationCount" );
        }
        String serverHostname = args[0];
        int serverPortnumber = Integer.parseInt( args[1] );
        int threadCount = Integer.parseInt( args[2] );
        int iterationCount = Integer.parseInt( args[3] );
        System.out.println ("Connecting to " + serverHostname + ":" + serverPortnumber + "..");
        try{
            Socket socket = new Socket (serverHostname, serverPortnumber);
            OutputStream out = socket.getOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(out);
            InputStream in = socket.getInputStream();
            ObjectInputStream is = new ObjectInputStream (in);
            //TODO: change numAccounts to 100
//            int numAccounts =100;
            int numAccounts =2;
            //1: sequentially create 100 threads
            int [] uids = createAccounts(os, is, numAccounts);
            //2: sequentially deposit 100 in each of these accounts
            deposit(os, is, uids, 100, numAccounts);
            //3: transfer using threads
            transfer(os, is, uids, threadCount, iterationCount);

            getTotalBalance(os,is,numAccounts,uids);
            socket.close();
        } catch (IOException e){
            e.printStackTrace();
        }

//        TCPClient client = new TCPClient(new Socket(serverHostname, serverPortnumber));
//        System.out.println ("Connected.");
//        client.start();
    }

    private static int[] createAccounts(ObjectOutputStream os, ObjectInputStream is , int numAccounts) {
        int[] uids = new int[numAccounts];
        try {
            for (int i = 0; i < numAccounts; i++) {

                Request createRequest = new CreateAccountRequest();
                os.writeObject(createRequest);


                CreateAccountResponse createResponse = (CreateAccountResponse) is.readObject();
                uids[i] = createResponse.getUid();
                System.out.printf("in client for account %d", uids[i]);
            }
        } catch (IOException e){
            e.printStackTrace ();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        return uids;
    }

    private static void deposit(ObjectOutputStream os,ObjectInputStream is , int[] uids, int amount, int numAccounts) {
        try {
            for (int i = 0; i < numAccounts; i++) {
//                OutputStream out1 = socket.getOutputStream();
//                ObjectOutputStream os1 = new ObjectOutputStream(out1);
                System.out.println("deposit before request");
                //
                Request depositRequest = new DepositRequest(uids[i],100);
                System.out.println("Created deposit request");
                os.writeObject(depositRequest);
                System.out.println("deposit before response");
                DepositResponse depositResponse = (DepositResponse) is.readObject();
                System.out.print("Operation status");
                System.out.println("deposit after read object");
                System.out.println(depositResponse.getStatus());
            }
        }catch (IOException e){
            e.printStackTrace ();
        } catch(ClassNotFoundException e){
            e.printStackTrace();
        }
    }
    private static void transfer(ObjectOutputStream os,ObjectInputStream is, int[] uids, int threadCount, int iterationCount){
//        Socket client = socket.accept ();
        for(int i=0;i<threadCount;i++){
            //create thread
            BankClient bankClient = new BankClient(os, is, uids, iterationCount);
            bankClient.start();
        }
    }

    public static int getTotalBalance(ObjectOutputStream os, ObjectInputStream is, int numAccounts, int[] uids){
        int total = 0;
        try {
            for (int i = 0; i < numAccounts; i++) {
                Request getBalanceRequest = new GetBalanceRequest(uids[i]);
                System.out.println("Created get balance request");
                os.writeObject(getBalanceRequest);
                System.out.printf("Total before response %d", total);
                GetBalanceResponse getBalanceResponse = (GetBalanceResponse) is.readObject();
                System.out.println("Current total is");
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

