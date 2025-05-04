package org.rolandort.formatter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rolandort.model.GelfMessage;
import org.rolandort.model.LogMessage;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DefaultGelfFormatterTest {

    private DefaultGelfFormatter formatter;
    private LogMessage logMessage;

    @BeforeEach
    void setUp() {
        formatter = new DefaultGelfFormatter();
        logMessage = new LogMessage();
        
        // Set up a sample log message with test data
        logMessage.setClientDeviceType("desktop");
        logMessage.setClientIp("192.168.87.52");
        logMessage.setClientIpClass("noRecord");
        logMessage.setClientStatus(403);
        logMessage.setClientRequestBytes(889);
        logMessage.setClientRequestReferer("graylog.org");
        logMessage.setClientRequestUri("/search");
        logMessage.setClientRequestUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        logMessage.setClientSrcPort(122);
        logMessage.setEdgeServerIp("10.0.151.71");
        logMessage.setEdgeStartTimestamp(1576929197.0);
        logMessage.setDestinationIp("172.16.153.30");
        logMessage.setOriginResponseBytes(821);
        logMessage.setOriginResponseTime(337000000);
    }

    @Test
    void formatValidMessageTest() {
        // When
        final GelfMessage result = formatter.formatMessage(logMessage);

        // Then
        assertNotNull(result, "GELF message should not be null");
        
        // Verify standard fields
        assertEquals("192.168.87.52", result.getHost(), "Host should be set to client IP");
        assertEquals("Request from 192.168.87.52 -> 172.16.153.30: /search (403)", result.getShortMessage(), "Short message should be formatted correctly");
        assertEquals("Request from desktop from 192.168.87.52:122 -> 172.16.153.30: /search (403)", result.getFullMessage(), "Full message should be formatted correctly");
        assertEquals(1, result.getLevel(), "Level should be set to 1");

        // Verify additional fields
        Map<String, Object> additionalFields = result.getAdditionalFields();
        assertNotNull(additionalFields, "Additional fields should not be null");
        assertFalse(additionalFields.isEmpty(), "Additional fields should not be empty");
        
        // Check specific additional fields
        assertEquals("desktop", additionalFields.get("clientDeviceType"));
        assertEquals("192.168.87.52", additionalFields.get("clientIp"));
        assertEquals("/search", additionalFields.get("clientRequestUri"));
        assertEquals(403, additionalFields.get("clientStatus"));
        assertEquals(1576929197.0, additionalFields.get("edgeStartTimestamp"));
    }

    @Test
    void formatEmptyMessageTest() {
        // Given
        LogMessage emptyMessage = new LogMessage();
        // Leave all fields as null
        
        // When
        final GelfMessage result = formatter.formatMessage(emptyMessage);
        
        // Then
        assertNotNull(result, "GELF message should not be null even with empty input");
        assertNull(result.getHost(), "Host should be null");
        assertEquals("Request from null -> null: null (null)", result.getShortMessage(), "Short message should handle null");
        assertEquals("Request from null from null:null -> null: null (null)", result.getFullMessage(), "Full message should handle null");
        assertEquals(1, result.getLevel(), "Level should still be set to 1");
        
        // Verify additional fields contain null values
        Map<String, Object> additionalFields = result.getAdditionalFields();
        assertNotNull(additionalFields, "Additional fields should not be null");
        assertFalse(additionalFields.isEmpty(), "Additional fields should not be empty");
        
        // All values should be null
        for (Object value : additionalFields.values()) {
            if (value != null) {
                // Some fields might be initialized with default values (e.g., primitives)
                // Only check reference types
                if (!(value instanceof Number)) {
                    assertNull(value, "Reference type fields should be null");
                }
            }
        }
    }
}
