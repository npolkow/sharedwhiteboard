package actors.events.socket.draw;

/**
 */
public class TextEvent extends AbstractDrawEvent {
    private int x;
    private int y;

    private String text;


    @Override
    public String getEventType() {
        return "TextEvent";
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
