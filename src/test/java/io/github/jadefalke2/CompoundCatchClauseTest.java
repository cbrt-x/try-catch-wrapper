package io.github.jadefalke2;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for the {@link CompoundCatchClause}
 */
public class CompoundCatchClauseTest {
	/**
	 * Tests the correctness of the constructor(s), specifically with regard to
	 * ensuring that invalid exception types are rejected.
	 */
	@Test
	public void testConstruct() {
		// First test valid cases. No exception should be thrown.
		new CompoundCatchClause(Throwable::printStackTrace, ArithmeticException.class, IOException.class);
		new CompoundCatchClause(Throwable::printStackTrace, RuntimeException.class, InterruptedException.class);
		// Then test invalid cases.
		assertThrows(IllegalArgumentException.class, () -> {
			new CompoundCatchClause(Throwable::printStackTrace, Exception.class, IOException.class);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			new CompoundCatchClause(Throwable::printStackTrace, IllegalArgumentException.class, RuntimeException.class);
		});
	}
}
