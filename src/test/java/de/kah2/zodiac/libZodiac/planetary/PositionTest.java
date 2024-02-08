package de.kah2.zodiac.libZodiac.planetary;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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

		assertThat(Position.isValidLatitude(lat)).as("Latitude validation failed: " + lat).isEqualTo(isValidLat);
		assertThat(Position.isValidLongitude(lng)).as("Longitude validation failed: " + lng).isEqualTo(isValidLng);

        final boolean isValid = isValidLat && isValidLng;

        try {

            final Position pos = new Position(lat, lng);

			assertThat(isValid).as("Construction of position should NOT throw exception").isTrue();

        } catch (IllegalArgumentException e) {

			assertThat(isValid).as("Construction of position should throw exception").isFalse();
        }
    }
}
