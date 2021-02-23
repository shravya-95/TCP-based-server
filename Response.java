import java.io.Serializable;

public abstract class Response implements Serializable {
    public String responseType;
    public Response(String responseType){
        super();
        this.responseType=responseType;
    }
    public String getResponseType(){
        return responseType;
    }

}



