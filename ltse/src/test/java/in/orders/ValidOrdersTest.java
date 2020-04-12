package in.orders;

import io.vavr.control.Try;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


public class ValidOrdersTest {
    @Test
    void shouldCreateOrders() {
        //given
        List<CSVRecord> records = new CsvFileParser("src/test/resources/trades.csv").parse();
        Set<String> firms = new HashSet<>(getFirms("src/test/resources/firms.txt"));
        Set<String> tickers = new HashSet<>(getTickers("src/test/resources/symbols.txt"));

        //when
        ValidOrders validOrders = new ValidOrders(Stream.of(records.get(0),
                records.get(11),
                records.get(10),
                records.get(86),
                records.get(87)
        ).map(record -> new Order(record, firms, tickers)));

        //then
        //then
        assertThat(validOrders.getOrders().get("Fidelity")).extracting("time")
                .containsExactly(LocalDateTime.parse("2017-05-10T10:00"), LocalDateTime.parse("2017-05-10T10:00:41"),LocalDateTime.parse("2017-05-10T10:00:40"));
    }

    @Test
    void shouldCreateAllTrades() {
        //given
        List<CSVRecord> records = new CsvFileParser("src/test/resources/trades.csv").parse();
        Set<String> firms = new HashSet<>(getFirms("src/test/resources/firms.txt"));
        Set<String> tickers = new HashSet<>(getTickers("src/test/resources/symbols.txt"));

        //when
        //when
        ValidOrders validOrders = new ValidOrders(records.stream().map(record -> new Order(record, firms, tickers)));

        //then
        assertThat(validOrders.getOrders().get("Ameriprise Financial")).hasSize(43);
        assertThat(validOrders.getOrders().get("Transamerica Financial")).hasSize(1);
        assertThat(validOrders.getOrders().get("Fidelity")).hasSize(50);
        assertThat(validOrders.getOrders().get("Edward Jones")).hasSize(45);
        assertThat(validOrders.getOrders().get("National Planning Corporation")).hasSize(45);
        assertThat(validOrders.getOrders().get("Charles Schwab")).hasSize(49);
        assertThat(validOrders.getOrders().get("Raymond James Financial")).hasSize(49);
        assertThat(validOrders.getOrders().get("AXA Advisors")).hasSize(46);
        assertThat(validOrders.getOrders().get("Wells Fargo AdvisorsWaddell & Reed")).isNull();
        assertThat(validOrders.getOrders().get("TD Ameritrade")).hasSize(46);
        assertThat(validOrders.getOrders().get("LPL Financial")).hasSize(49);
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
}
