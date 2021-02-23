public class DepositRequest extends Request {
    public String uid;
    public int amount;
    public DepositRequest() {
        super("Deposit");
    }
}
