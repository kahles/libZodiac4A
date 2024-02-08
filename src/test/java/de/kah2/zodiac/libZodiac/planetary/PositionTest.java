package de.kah2.zodiac.libZodiac.planetary;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link Position}.
 */
public class PositionTest {

    @Test
    public void testValidation() {

        checkValidation(false,-90.1, true,1);
        checkValidation(false,90.1, true,1);
        checkValidation(true,-90, true,-1);
        checkValidation(true,90, true,1);
        checkValidation(true,0, true,0);
        checkValidation(true,-1, true,-180);
        checkValidation(true,1, true,180);
        checkValidation(true,1, false,-180.1);
        checkValidation(true,1, false,180.1);
    }

    private void checkValidation(boolean isValidLat, double lat, boolean isValidLng, double lng) {

        assertEquals( isValidLat, Position.isValidLatitude(lat), "Latitude validation failed: " + lat );
        assertEquals( isValidLng, Position.isValidLongitude(lng), "Longitude validation failed: " + lng );

        final boolean isValid = isValidLat && isValidLng;

        try {

            final Position pos = new Position(lat, lng);

            assertTrue(isValid, "Construction of position should NOT throw exception");

        } catch (IllegalArgumentException e) {

            assertFalse(isValid, "Construction of position should throw exception");
        }
    }
}
