import java.net.*;
import java.io.*;
public class TCPClient extends Thread{
//    //TODO: Create request object based on type of request
//    protected Request request;
//    protected String host, file;
//    protected int port;
//    protected Socket socket;
//    TCPClient (Socket socket){
//        System.out.println ("New client thread");
//        this.socket=socket;
//    }
//    public void run(){
//        try {
//            //TODO: understand the types of io
//            OutputStream rawOut = socket.getOutputStream();
//            InputStream rawIn = socket.getInputStream();
//            BufferedReader buffreader = new BufferedReader(new InputStreamReader(rawIn));
//            PrintWriter serverWriter = new PrintWriter(new OutputStreamWriter(rawOut));
//            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
//
//            String line;
//            while ((line = keyboard.readLine()) != null) {
//                serverWriter.println(line);
//                serverWriter.flush();
//            }
//            socket.shutdownOutput();
//            /*    while ( ( line = buffreader.readLine() ) != null ) {
//        System.out.println( line );    }*/
//            while (buffreader.ready()) {
//                if ((line = buffreader.readLine()) != null) {
//                    System.out.println(line);
//                }
//            }
//
//
//        } catch (IOException e){
//            e.printStackTrace ();
//        }
//
//
//    }
    public static void main (String args[]) throws IOException {
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
//        if ( args.length != 2 ) {
//            throw new RuntimeException( "hostname and port number as arguments" );
//        }
//        String host = args[0];
//        int  port = Integer.parseInt( args[1] );
//        String requestType = args[2];
//        System.out.println ("Connecting to " + host + ":" + port + "..");
//        TCPClient client = new TCPClient(new Socket(host, port));
//        System.out.println ("Connected.");
//        client.start();
    }
}

