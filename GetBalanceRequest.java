public class GetBalanceRequest extends Request {
    public int uid;
    public GetBalanceRequest(int uid)
    {
        super("getBalance");
        this.uid=uid;
        System.out.println("In GetBalanceRequest");
    }
    public int getUid(){
        return this.uid;
    }


}
