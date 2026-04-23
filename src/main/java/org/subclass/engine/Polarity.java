package org.subclass.engine;

/**
 * Logical polarity of a node in a proof net.
 */
public enum Polarity {
    POSITIVE,
    NEGATIVE;

    /**
     * Get the opposite polarity.
     * @return NEGATIVE if POSITIVE, and vice versa.
     */
    public Polarity opposite() {
        return this == POSITIVE ? NEGATIVE : POSITIVE;
    }
}
