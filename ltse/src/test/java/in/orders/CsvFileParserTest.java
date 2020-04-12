package in.orders;

import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class CsvFileParserTest {
    private final CsvFileParser underTest = new CsvFileParser("src/test/resources/trades.csv");

    @Test
    void shouldParseInput() {
        //when
        List<CSVRecord> records = underTest.parse();

        //then
        assertThat(records.size()).isEqualTo(554);
    }

}
