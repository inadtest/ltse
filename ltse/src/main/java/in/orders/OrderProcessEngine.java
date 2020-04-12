package in.orders;

import io.vavr.control.Try;
import lombok.Getter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class OrderProcessEngine {
    @Getter
    private final Map<String, OrdersByBroker> ordersByBroker;
    @Getter
    private final Set<String> firms;
    @Getter
    private final Set<String> tickers;
    private final String acceptedPath = "src/test/output/accepted.csv";
    private final String rejectedPath = "src/test/output/rejected.csv";
    private CSVPrinter acceptedPrinter;
    private CSVPrinter rejectedPrinter;

    OrderProcessEngine(String firmNames, String tickerNames, String ordersFile) {
        ordersByBroker = new HashMap<>();
        firms = new HashSet<>();
        tickers = new HashSet<>();
        getFirms(firmNames).stream().forEach(rec -> firms.add(rec));
        getTickers(tickerNames).stream().forEach(rec -> tickers.add(rec));

        writeHeader();

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
            value.writeOutput(acceptedPrinter, rejectedPrinter);
        });

        try {
            acceptedPrinter.close();
            rejectedPrinter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void writeHeader() {
        try {

            acceptedPrinter = new CSVPrinter(new FileWriter(acceptedPath), CSVFormat.EXCEL);
            acceptedPrinter.printRecord("Time stamp", "broker", "sequence id", "type", "Symbol", "Quantity", "Price", "Side");
            acceptedPrinter.flush();

            rejectedPrinter = new CSVPrinter(new FileWriter(rejectedPath), CSVFormat.EXCEL);
            rejectedPrinter.printRecord("Time stamp", "broker", "sequence id", "type", "Symbol", "Quantity", "Price", "Side");
            rejectedPrinter.flush();

        } catch (IOException e) {
            throw new RuntimeException("File not Found");
        }
    }
}
