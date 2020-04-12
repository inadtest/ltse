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
        Set<Integer> brokerSequenceIds = new HashSet<>();
        ListIterator<Order> iter = accepted.listIterator();

        while(iter.hasNext()) {
            Order order = iter.next();
            if (brokerSequenceIds.contains(order.getSequenceId())) {
                rejected.add(order);
            } else {
                brokerSequenceIds.add(order.getSequenceId());
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
    }

    void writeOutput(CSVPrinter acceptedPrinter, CSVPrinter rejectedPrinter) {
        if (accepted.size() > 0) {
            try {
                writeToFile(true,  acceptedPrinter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (rejected.size() > 0) {
            try {
                writeToFile(false,  rejectedPrinter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void writeToFile(boolean isAccepted, CSVPrinter printer) throws IOException {
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
                printer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }
}
