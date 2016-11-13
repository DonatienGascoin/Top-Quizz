package cpe.top.quizz.beans;

/**
 *
 * @author Donatien
 * @since 10/11/2016
 * @version 0.1
 */

public enum ReturnCode {
    /**
     * No error
     */
    ERROR_000,
    /**
     * ObjectNotFound
     */
    ERROR_100,
    /**
     * Database Unreachable
     */
    ERROR_200,
    /**
     * Pseudo already exist
     */
    ERROR_300,
    /**
     * Email already exist
     */
    ERROR_400;
}
