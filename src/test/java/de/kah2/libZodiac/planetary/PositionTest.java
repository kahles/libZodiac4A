package de.kah2.libZodiac.planetary;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

        assertEquals( "Latitude validation failed: " + lat, isValidLat, Position.isValidLatitude(lat) );
        assertEquals( "Longitude validation failed: " + lng, isValidLng, Position.isValidLongitude(lng) );

        final boolean isValid = isValidLat && isValidLng;

        try {

            final Position pos = new Position(lat, lng);

            assertTrue("Construction of position should NOT throw exception", isValid);

        } catch (IllegalArgumentException e) {

            assertFalse("Construction of position should throw exception", isValid);
        }
    }
}
