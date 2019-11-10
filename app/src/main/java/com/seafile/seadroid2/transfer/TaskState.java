package com.seafile.seadroid2.transfer;

/**
 * Task state
 */
public enum TaskState {
    /**
     * Init task state.
     */
    INIT,
    /**
     * Transferring task state.
     */
    TRANSFERRING,
    /**
     * Finished task state.
     */
    FINISHED,
    /**
     * Cancelled task state.
     */
    CANCELLED,
    /**
     * Failed task state.
     */
    FAILED }
