public class DepositResponse extends Response {
    boolean status;
    //TODO: string or boolean?
    public DepositResponse(boolean status) {
        this.status = status;
    }
}
