public class GetBalanceRequest extends Request {
    public int uid;
    public GetBalanceRequest(int uid)
    {
        super("getBalance");
        System.out.println("In GetBalanceRequest");
    }
    public int getUid(){
        return this.uid;
    }


}
