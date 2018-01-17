package de.kah2.libZodiac.zodiac;

/**
 * This class represents the four elements, the zodiac signs belong to. These
 * elements say to which {@link Category} a day belongs, which {@link PlantPart}
 * is most affected by this element and which {@link FoodElement} is absorbed
 * most.
 * 
 * @author kahles
 */
public enum ZodiacElement {

	FIRE(Category.WARMTH, PlantPart.FRUIT, FoodElement.PROTEIN), WATER(Category.WATER, PlantPart.LEAF,
			FoodElement.CARBOHYDRATE), AIR(Category.LIGHT, PlantPart.FLOWER,
					FoodElement.FAT), EARTH(Category.COLDNESS, PlantPart.ROOT, FoodElement.SALT);

	public enum Category {
		WARMTH, WATER, LIGHT, COLDNESS
	}

	public enum PlantPart {
		ROOT, LEAF, FLOWER, FRUIT
	}

	public enum FoodElement {
		PROTEIN, CARBOHYDRATE, FAT, SALT
	}

	private final Category dayCategory;
	private final PlantPart plantPart;
	private final FoodElement foodElement;

	ZodiacElement(final Category category, final PlantPart plantPart, final FoodElement foodElement) {
		this.dayCategory = category;
		this.plantPart = plantPart;
		this.foodElement = foodElement;
	}

	public static ZodiacElement of(final ZodiacSign sign) {
		ZodiacElement type = null;
		switch (sign) {
		case ARIES:
		case LEO:
		case SAGITTARIUS:
			type = FIRE;
			break;
		case CAPRICORN:
		case TAURUS:
		case VIRGO:
			type = EARTH;
			break;
		case PISCES:
		case CANCER:
		case SCORPIO:
			type = WATER;
			break;
		case AQUARIUS:
		case GEMINI:
		case LIBRA:
			type = AIR;
			break;
		}
		return type;
	}

	/**
	 * @return the day category corresponding to this zodiac element.
	 */
	public Category getDayCategory() {
		return this.dayCategory;
	}

	/**
	 * @return the plant part most affected by this zodiac element.
	 */
	public PlantPart getPlantPart() {
		return this.plantPart;
	}

	/**
	 * @return the food element most absorbed at this zodiac element
	 */
	public FoodElement getFoodElement() {
		return this.foodElement;
	}
}
