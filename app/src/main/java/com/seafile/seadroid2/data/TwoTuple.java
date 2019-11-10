package com.seafile.seadroid2.data;

/**
 * The type Two tuple.
 *
 * @param <X> the type parameter
 * @param <Y> the type parameter
 */
public class TwoTuple<X, Y> {
    private X x;
    private Y y;

    /**
     * Instantiates a new Two tuple.
     *
     * @param x the x
     * @param y the y
     */
    public TwoTuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets first.
     *
     * @return the first
     */
    public X getFirst() {
        return x;
    }

    /**
     * Gets second.
     *
     * @return the second
     */
    public Y getSecond() {
        return y;
    }

    /**
     * New instance two tuple.
     *
     * @param <U> the type parameter
     * @param <V> the type parameter
     * @param u   the u
     * @param v   the v
     * @return the two tuple
     */
    public static <U,V> TwoTuple<U, V> newInstance(U u, V v) {
        return new TwoTuple<U,V>(u, v);
    }
}
