public class TransferRequest extends Request {
    public String sourceUID, targetUID;
    public int amount;
    public TransferRequest() {
        super("Transfer");
    }


}
