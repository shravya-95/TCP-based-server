public class TransferRequest extends Request {
    public int sourceUid, targetUid;
    public int amount;
    public TransferRequest(int sourceUid, int targetUid, int amount) {
        super("transfer");
        System.out.println("In TransferRequest");
    }


}
