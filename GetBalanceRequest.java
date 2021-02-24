public class GetBalanceRequest extends Request {
//    public String uid;
    public GetBalanceRequest(int uid)
    {
        super("getBalance");
        System.out.println("In GetBalanceRequest");
    }


}
