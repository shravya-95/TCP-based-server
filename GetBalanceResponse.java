public class GetBalanceResponse extends Response {
    public int balance;
    public GetBalanceResponse(int balance)
    {
        this.balance = balance;
    }
    public int getBalance(){
        return this.balance;
    }
}
