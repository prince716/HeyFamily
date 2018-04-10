package fanshawe.heyfamily;

/**
 * Created by Prince on 2018-04-10.
 */

public class Messages {
    private String message;
    private String type;
    private boolean seen;

    public Messages() {
    }

    public Messages(String message, String type, boolean seen) {
        this.message = message;
        this.type = type;
        this.seen = seen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
