import java.io.Serializable;

public abstract class Request implements Serializable {

    public String requestType;
    public Request request;
    public Request(){
        this.request = new CreateAccountRequest();
        this.requestType="CreateAccount";
    }
    public Request(String uid){

    }
    public String getRequestType(){
        return requestType;
    }
    public void addRecord(String uid){
        // records.put(uid, )//balan
    }
}

