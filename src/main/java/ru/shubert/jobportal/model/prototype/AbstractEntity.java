package ru.shubert.jobportal.model.prototype;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shubert.jobportal.strategy.ORMHelper;


import javax.persistence.*;
import java.io.Serializable;

/**
 * Implementation of {@link IEntity} with some useful constants
 * It must never be persisted to datastorage directly hence requires subclassing.
 * Contains unique identifier, hashCode and equals overloaded methods
 * Also exposes some constants.
 */
@MappedSuperclass
public abstract class AbstractEntity implements IEntity, Serializable {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractEntity.class);

    public static final int LONG_STRING = 256;
    public static final int MEDIUM_STRING = 100;
    public static final int SHORT_STRING = 50;
    public static final int SMALL_STRING = 8;



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    public AbstractEntity() {
        LOGGER.debug("creating " + this.getClass().getName());
    }

    /**
     * Unique identifier assigned by datastorage. Must not be set manually.
     *
     * @return identifier.
     */
    public Serializable getId() {
        return id;
    }

    /**
     * Returns unique within the bounds of it's class identifier
     *
     * @param id to be set
     */
    public void setId(@NotNull Serializable id) {
        this.id = (Long) id;
    }

    /**
     * Compares <code>this</code> with provided one.
     * Two object are equal in terms of domain logic if
     * <ul>
     * <li> their references are equals or
     * <li> their classes are equal and their id is not null and are equal.
     * </ul>
     * It is not explicit but there are no problem in comparing objects that was not persisted yet.
     * They just can be compared only by references.
     *
     * @param obj to be compared
     * @return true if objects are equal, false otherwise.
     */
    @SuppressWarnings({"EqualsWhichDoesntCheckParameterClass"})
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null) {
            return false;  // fast null-check 
        } else if (ORMHelper.getClass(obj) == ORMHelper.getClass(this)) {
            AbstractEntity ae = (AbstractEntity) obj;
            return getId() != null && getId().equals(ae.getId());
        }

        return false;
    }

    /**
     * return hash code for Entity object. First it checks if identifier already supplied and if that so
     * calculates hash basing on class hash code (to somehow distinct our entity from entityes of another
     * class) and ID.
     * If there are no id set yet, e.g. object was not persisted yet it calculates hash codes basing on class and
     * hashCode method of Long value. It may be sensibly to revisit current realization
     * <p/>
     * Argument for current implementation: There are nothing extreme in getting hash of unpersisted
     * object (with unassigned id) becouse his class and reference may serve as his hashcode.
     *
     * @return hashCode for domain entity object
     */
    @Override
    public int hashCode() {
        return 217 + (getId() != null ? getId().hashCode() : 0);
    }

    public boolean isPersisted(){
        return getId() != null;
    }
}
