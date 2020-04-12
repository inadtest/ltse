package in.orders;

public class Application {
    public static void main(String[] args) {
        OrderProcessEngine engine = new OrderProcessEngine(args[0], args[1], args[2]);
        engine.process();
    }
}
