package in.orders;

import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static in.orders.Side.Buy;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderTest {
    @Test
    void shouldCreateTrade() {
        //given
        List<CSVRecord> records = new CsvFileParser("src/test/resources/trades.csv").parse();

        //when
        Order order  = new Order(records.get(3), new HashSet<>(), new HashSet<>());

        //then
        assertEquals(order.getBroker(), "National Planning Corporation");
        assertEquals(order.getTime(), LocalDateTime.parse("2017-05-10T10:00:03"));
    }

}
