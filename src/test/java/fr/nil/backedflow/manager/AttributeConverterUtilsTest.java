package fr.nil.backedflow.manager;

import fr.nil.backedflow.services.utils.AttributeConverterUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AttributeConverterUtilsTest {

    private AttributeConverterUtils converter;

    @BeforeEach
    public void setUp() {
        converter = new AttributeConverterUtils();
    }

    @Test
    void testConvertToDatabaseColumnWithNonNullValue() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 9, 12, 10, 10);
        Timestamp expectedTimestamp = Timestamp.valueOf(localDateTime);

        Timestamp result = converter.convertToDatabaseColumn(localDateTime);

        assertEquals(expectedTimestamp, result);
    }

    @Test
    void testConvertToDatabaseColumnWithNullValue() {
        assertNull(converter.convertToDatabaseColumn(null));
    }

    @Test
    void testConvertToEntityAttributeWithNonNullValue() {
        LocalDateTime expectedLocalDateTime = LocalDateTime.of(2023, 9, 12, 10, 10);
        Timestamp timestamp = Timestamp.valueOf(expectedLocalDateTime);

        LocalDateTime result = converter.convertToEntityAttribute(timestamp);

        assertEquals(expectedLocalDateTime, result);
    }

    @Test
    void testConvertToEntityAttributeWithNullValue() {
        assertNull(converter.convertToEntityAttribute(null));
    }
}