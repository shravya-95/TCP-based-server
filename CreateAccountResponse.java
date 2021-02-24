public class CreateAccountResponse extends Response {
    public int uid;
    public CreateAccountResponse(int uid) {
        this.uid = uid;
    }
    public int getUid(){
        return this.uid;
    }
}
