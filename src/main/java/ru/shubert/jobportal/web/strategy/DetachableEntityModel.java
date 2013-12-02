package ru.shubert.jobportal.web.strategy;


import org.apache.wicket.model.LoadableDetachableModel;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.shubert.jobportal.model.AbstractEntity;
import ru.shubert.jobportal.service.IService;

/**
 *
 *
 */
public class DetachableEntityModel<T> extends LoadableDetachableModel<T> {

    private static final Long serialVersionUID = 1L;

    private IService service;
    private ObjectId id;
    private Class<T> tClass;

    public DetachableEntityModel(IService service) {
        this(null, service);
    }

    public DetachableEntityModel(@Nullable T obj, @NotNull IService service) {
        this.service = service;
        setObject(obj);
    }

    /**
     * Load object again
     *
     * @return loaded object
     */
    @Override
    protected T load() {
        return (tClass == null || id == null) ? null : service.findOne(id, tClass);
    }

    /**
     * Queryes object for it's ID and class for further loading
     * Manually loads the model with the specified object. Subsequent calls to {@link #getObject()}
     * will return {@code object} until {@link #detach()} is called.
     * May throw type cast exception as all T must implement IEntity.
     *
     * @param object The object to set into the model
     */
    @SuppressWarnings({"unchecked"})
    @Override
    public void setObject(@Nullable T object) {
        super.setObject(object);
        if (object != null) {
            id = ((AbstractEntity) object).getId();
            tClass = (Class<T>) object.getClass();
        } else {
            tClass = null;
            id = null;
        }
    }

    /**
     * required by {@link org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy}
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 31;
    }

    /**
     * required by {@link org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy}
     * Note: it DOESN'T compare model itself rather it's Object!
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof DetachableEntityModel) {
            DetachableEntityModel other = (DetachableEntityModel) obj;
            return ((this.tClass == other.tClass) &&
                    ((id != null && id.equals(other.id)) || (id == other.id)));
        }
        return false;
    }
}
