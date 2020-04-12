package in.orders;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.EnumUtils;

import java.time.LocalDateTime;
import java.util.Set;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class Order implements Comparable<Order> {
    private final LocalDateTime time;
    private final String broker;
    private final Integer sequenceId;
    private final String type;
    private final String symbol;
    private final Long quantity;
    private final Double price;
    private final Side side;
    private Set<String> firms;
    private Set<String> tickers;

    Order(CSVRecord record, Set<String> firms, Set<String> tickers) {
        this.time = LocalDateTime.parse(record.get("Time stamp"), ofPattern("dd/M/yyyy HH:mm:ss"));
        this.broker = record.get("broker");
        this.sequenceId = Integer.valueOf(record.get("sequence id"));
        this.type = record.get("type");
        this.symbol = record.get("Symbol");
        this.quantity = Long.valueOf(record.get("Quantity"));
        this.price = Double.valueOf(record.get("Price"));
        this.side = EnumUtils.getEnum(Side.class, record.get("Side"));
        this.firms = firms;
        this.tickers = tickers;
    }

    public Order(LocalDateTime time, String broker, int id, String type, String tickr, long qty, double price, String side) {
        this.time = time;
        this.broker = broker;
        this.sequenceId = Integer.valueOf(id);
        this.type = type;
        this.symbol = tickr;
        this.quantity = Long.valueOf(qty);
        this.price = Double.valueOf(price);
        this.side = EnumUtils.getEnum(Side.class, side);
    }

    boolean isValid() {
        return nonNull(time) &&
               isNotBlank(broker) && firms.contains(broker) &&
                nonNull(sequenceId) &&
                isNotBlank(type) &&
                isNotBlank(symbol) && tickers.contains(symbol) &&
                nonNull(quantity) &&
                nonNull(price) &&
                nonNull(side);
    }

    @Override
    public int compareTo(Order o) {
        return this.time.compareTo(o.getTime());
    }
}

enum Side {
    Buy, Sell
}
