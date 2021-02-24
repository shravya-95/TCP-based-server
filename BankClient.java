import java.net.*;
import java.io.*;
public class BankClient extends Thread{
//    //TODO: Create request object based on type of request
    protected Request request;
    protected String host, file;
    protected int port;
    protected Socket socket;

    BankClient(Socket socket){
        System.out.println ("New client thread");
        this.socket=socket;
    }
    public void run(){
        try {
//            //TODO: understand the types of io
            OutputStream rawOut = socket.getOutputStream();
            InputStream rawIn = socket.getInputStream();
            BufferedReader buffreader = new BufferedReader(new InputStreamReader(rawIn));
            PrintWriter serverWriter = new PrintWriter(new OutputStreamWriter(rawOut));
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

            String line;
            while ((line = keyboard.readLine()) != null) {
                serverWriter.println(line);
                serverWriter.flush();
            }
            socket.shutdownOutput();
            /*    while ( ( line = buffreader.readLine() ) != null ) {
                System.out.println( line );    }*/
            while (buffreader.ready()) {
                if ((line = buffreader.readLine()) != null) {
                    System.out.println(line);
                }
            }


        } catch (IOException e){
            e.printStackTrace ();
        }


    }
    public static void main (String args[]){
//        System.out.println("main account");
//        Request r1= new CreateAccountRequest();
//        System.out.println("main deposit");
//        Request r2= new DepositRequest(1,2);
//        System.out.println("main balance");
//        Request r3= new GetBalanceRequest(1);
//        System.out.println("main transfer");
//        Request r4= new TransferRequest(1,2,3);

//        InetAddress  server  = null;
//        Socket      sock = null;
        if ( args.length != 2 ) {
            throw new RuntimeException( "Syntax: java BankClient serverHostname severPortnumber threadCount iterationCount" );
        }
        String serverHostname = args[0];
        int  serverPortnumber = Integer.parseInt( args[1] );
//        int threadCount = Integer.parseInt( args[2] );
//        int iterationCount = Integer.parseInt( args[3] );
        System.out.println ("Connecting to " + serverHostname + ":" + serverPortnumber + "..");
        try{
            Socket socket = new Socket (serverHostname, serverPortnumber);
            OutputStream out = socket.getOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(out);
//            int numAccounts =100;
            int numAccounts =1;
            //sequentially create 100 threads
            int [] uids = createAccounts(os, socket, numAccounts);
            //sequentially deposit 100 in each of these accounts
            depositAccounts(socket, uids, 100, numAccounts);

            socket.close();
        } catch (IOException e){
            e.printStackTrace();
        }

//        TCPClient client = new TCPClient(new Socket(serverHostname, serverPortnumber));
//        System.out.println ("Connected.");
//        client.start();
    }

    private static int[] createAccounts(ObjectOutputStream os, Socket socket, int numAccounts) {
        int[] uids = new int[numAccounts];
        try {
            for (int i = 0; i < numAccounts; i++) {

                Request createRequest = new CreateAccountRequest();
                os.writeObject(createRequest);

                InputStream in = socket.getInputStream();
                ObjectInputStream is = new ObjectInputStream (in);
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

    private static void depositAccounts(Socket socket, int[] uids, int amount, int numAccounts) {
        try {
            for (int i = 0; i < numAccounts; i++) {
//                OutputStream out1 = socket.getOutputStream();
//                ObjectOutputStream os1 = new ObjectOutputStream(out1);
                System.out.println("deposit before request");
                //
                Request depositRequest = new DepositRequest(uids[i],100);
                System.out.println("Created deposit request");
                os1.writeObject(depositRequest);
                System.out.println("deposit before response");
                InputStream in = socket.getInputStream();
                ObjectInputStream is = new ObjectInputStream (in);
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
}

