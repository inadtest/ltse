package in.orders;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class OrdersByBrokerTest {
    private final Order trade1 = new Order(LocalDateTime.parse("2017-05-10T10:00:03"),"National Planning Corporation", 1, "K", "LGHT", 400L, 140D, "Buy");
    private final Order trade2 = new Order(LocalDateTime.parse("2017-05-10T10:00:04"),"National Planning Corporation", 1, "K", "LGHT", 500L, 140D, "Buy");
    private final Order trade3 = new Order(LocalDateTime.parse("2017-05-10T10:00:05"),"National Planning Corporation", 2, "K", "LGHT", 600L, 140D, "Buy");
    private final Order trade4 = new Order(LocalDateTime.parse("2017-05-10T10:00:06"),"National Planning Corporation", 3, "K", "LGHT", 700L, 140D, "Buy");
    private final Order trade5 = new Order(LocalDateTime.parse("2017-05-10T10:00:08"),"National Planning Corporation", 4, "K", "LGHT", 800L, 140D, "Buy");

    @Test
    void shouldRemoveDuplicateIds() {
        //given
        OrdersByBroker brokerOrders = new OrdersByBroker(Arrays.asList(trade3, trade2, trade1));

        //when
        brokerOrders.rejectRepeatedIds();

        //then
        assertThat(brokerOrders.getAccepted()).containsExactly(trade1, trade2, trade3);
        assertThat(brokerOrders.getRejected()).containsExactly(trade2);
    }

    @Test
    void shouldRemoveRedundantTradesBasedOnTime() {
        //given
        OrdersByBroker brokerOrders = new OrdersByBroker(Arrays.asList(trade3, trade5, trade1, trade4));

        //when
        brokerOrders.rejectBasedOntime();
        //then
        assertThat(brokerOrders.getAccepted()).containsExactly(trade1, trade3, trade4);
        assertThat(brokerOrders.getRejected()).containsExactly(trade5);
    }

    @Test
    void shouldRemoveAllRedundantTrades() {
        //given
        OrdersByBroker brokerOrders = new OrdersByBroker(Arrays.asList(trade3, trade2, trade5, trade1, trade4));

        //when
        brokerOrders.rejectRepeatedIds();
        brokerOrders.rejectBasedOntime();

        //then
        assertThat(brokerOrders.getAccepted()).containsExactly(trade1, trade3, trade4);
        assertThat(brokerOrders.getRejected()).containsExactly(trade2, trade5);
    }

}
