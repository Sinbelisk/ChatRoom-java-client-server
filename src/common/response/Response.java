package common.response;

public class Response {
    private final String id;
    private final ResponseStatus status;
    private final String message;

    public Response(String id, ResponseStatus status, String message) {
        this.id = id;
        this.status = status;
        this.message = message;
    }

    @Override
    public String toString() {
        return id + status + (message != null ? ";" + message : "");
    }

    public byte[] getBytes(){
        return toString().getBytes();
    }

    public String getId() {
        return id;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
