package in.orders;

import io.vavr.control.Try;

import lombok.Getter;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.csv.CSVFormat.EXCEL;

public class OrderProcessEngine {
    @Getter
    private final Map<String, OrdersByBroker> ordersByBroker;
    @Getter
    private final Set<String> firms;
    @Getter
    private final Set<String> tickers;

    OrderProcessEngine(String firmNames, String tickerNames, String ordersFile) {
        ordersByBroker = new HashMap<>();
        firms = new HashSet<>();
        tickers = new HashSet<>();
        getFirms(firmNames).stream().forEach(rec -> firms.add(rec));
        getTickers(tickerNames).stream().forEach(rec -> tickers.add(rec));

        new ValidOrders(new CsvFileParser(ordersFile).parse().stream().map(record -> new Order(record, firms, tickers))).getOrders()
                .forEach((key, value) -> ordersByBroker.put(key, new OrdersByBroker(value)));
    }

    List<String> getFirms(String firmnames) {
        return Try.of(() ->
                FileUtils.readLines(new File(firmnames), "UTF-8")).getOrElseThrow(() -> new RuntimeException("File not found")
        );
    }

    List<String> getTickers(String tickerNames) {
        return Try.of(() ->
                FileUtils.readLines(new File(tickerNames), "UTF-8")).getOrElseThrow(() -> new RuntimeException("File not found")
        );
    }

    void process() {
        ordersByBroker.forEach((key, value) -> {
            value.rejectRepeatedIds();
            value.rejectBasedOntime();
        });
    }
}
