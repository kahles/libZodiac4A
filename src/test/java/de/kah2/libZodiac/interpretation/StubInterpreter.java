package de.kah2.libZodiac.interpretation;

import de.kah2.libZodiac.Day;

public class StubInterpreter extends Interpreter {

	protected final String BEST = "This is best.";
	protected final String GOOD = "This is good.";
	protected final String BAD = "This is bad.";
	protected final String WORST = "This is worst.";

	public StubInterpreter(final Day dayToInterpret) {
		super(dayToInterpret);
	}

	@Override
	public void doInterpretations() {
		this.addBest(this.BEST);
		this.addGood(this.GOOD);
		this.addBad(this.BAD);
		this.addWorst(this.WORST);
	}
}
