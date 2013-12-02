package ru.shubert.jobportal.model.prototype;
import org.apache.wicket.IClusterable;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Defines base interface for persistable classes.
 * The only thing one may say about them, they have unique identifier
 * Already prepared for compound keys as holds no information about it true nature
 */
public interface IEntity extends IClusterable {
    /**
     * Returns unique identifier set by persistent storage. It may be null if this object was never persisted.
     * . It may be null if this object was never persisted.
     *
     * @return datastorage generated ID
     */
    Serializable getId();


    /**
     * Set identifier. Must be never called manually as it set by storage.
     *
     * @param id to be set
     */
    void setId(@NotNull Serializable id);

    /**
     * @see Object#equals(Object)
     */
    boolean equals(Object obj);

    /**
     * @see Object#hashCode()
     */
    int hashCode();
}
