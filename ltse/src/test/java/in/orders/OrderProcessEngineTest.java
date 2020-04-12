package in.orders;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderProcessEngineTest {

    @Test
    void shouldRemoveDuplicateIds() {
        //given
        OrderProcessEngine engine = new OrderProcessEngine("src/test/resources/firms.txt",
                                                        "src/test/resources/symbols.txt",
                                                        "src/test/resources/trades.csv");

        engine.process();

        //then
        assertThat(engine.getOrdersByBroker().get("AXA Advisors").getAccepted()).hasSize(45);
        assertThat(engine.getOrdersByBroker().get("AXA Advisors").getRejected()).hasSize(1);


    }

}
