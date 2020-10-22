package ChainOfResponsibility;

public abstract class Layer {
    private Layer next;

    public void setNext(Layer nextLayer) {
            next = nextLayer;
    }

    public abstract void send();
    public abstract void receive();
}
