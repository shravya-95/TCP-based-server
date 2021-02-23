import java.io.Serializable;

public abstract class Request implements Serializable {

    public String requestType;
    public Request(String requestType){
        super();
        this.requestType=requestType;
    }
    public String getRequestType(){
        return requestType;
    }
    public void addRecord(String uid){
        // records.put(uid, )//balan
    }
}

