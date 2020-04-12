package in.orders;

import lombok.Getter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Getter
public class OrdersByBroker {
    private final List<Order> accepted;
    private final List<Order> rejected;
    private final String acceptedPath = "src/test/output/accepted.csv";
    private final String rejectedPath = "src/test/output/rejected.csv";

    OrdersByBroker() {
        this.accepted = new ArrayList<>();
        this.rejected = new ArrayList<>();
    }

    OrdersByBroker(List<Order> orders) {
        this();
        accepted.addAll(orders);
        Collections.sort(this.accepted);
    }

    void rejectRepeatedIds() {
        Set<String> brokerSequenceIds = new HashSet<>();
        ListIterator<Order> iter = accepted.listIterator();

        while(iter.hasNext()) {
            Order order = iter.next();
            if (brokerSequenceIds.contains(order.getBroker() + order.getSequenceId())) {
                rejected.add(order);
            } else {
                brokerSequenceIds.add(order.getBroker() + order.getSequenceId());
            }
        }
    }

    void rejectBasedOntime() {
        for (int i = 0; i < accepted.size() - 1; i++) {
            Order current = accepted.get(i);
            int counter = 0;
            for (int j = i + 1; j < accepted.size(); j++) {
                Order next = accepted.get(j);
                if (rejected.contains(next) || next.getTime().isAfter(current.getTime().plusMinutes(1))) {
                    break;
                }
                if (counter++ >= 2) {
                    rejected.add(next);
                }
            }
        }
        Collections.sort(rejected);
        accepted.removeAll(rejected);
        writeOutput();
    }

    void writeOutput() {
        if (accepted.size() > 0) {
            writeToFile(true, acceptedPath);
        }
        if (rejected.size() > 0) {
            writeToFile(false, rejectedPath);
        }
    }

    void writeToFile(boolean isAccepted, String fileName) {
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(fileName), CSVFormat.EXCEL)) {
            printer.printRecord("Time stamp","broker", "sequence id", "type", "Symbol", "Quantity", "Price", "Side");

            List<Order> list;
            if (isAccepted) {
                list = accepted;
            } else {
                list = rejected;
            }
            list.forEach(order -> {
                String time = order.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                try {
                    printer.printRecord(time, order.getBroker(), order.getSequenceId().toString(), order.getType(), order.getSymbol(), order.getQuantity().toString(), order.getPrice().toString(), order.getSide().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException ex) {
           throw new RuntimeException("File not Found");
        }
    }

}
