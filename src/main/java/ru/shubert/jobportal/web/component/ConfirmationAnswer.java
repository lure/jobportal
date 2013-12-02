package ru.shubert.jobportal.web.component;

import org.apache.wicket.IClusterable;

/**
 * One-variable holder for user choice.
 * Holds true if user choose "yes" and false otherwise.
 *
 */
public class ConfirmationAnswer implements IClusterable {

    private boolean confirmed;

    /**
     * Counstruct new answer with initial value set to parameter
     */
    public ConfirmationAnswer() {
        this.confirmed = false;
    }


    /**
     * Returns holded value
     * @return true|false
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * Set answer value
     * @param confirmed to hold
     */
    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}
