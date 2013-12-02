package ru.shubert.jobportal.web.strategy;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.WicketObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shubert.jobportal.dao.QueryParams;
import ru.shubert.jobportal.service.IService;

import java.util.Iterator;

/**
 * Data provider for {@link org.apache.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable}
 * It's mission is to provide filtered and sorted cortege. Sorting and pagination set in {@link QueryParams} class
 * while current filter state is held by {@link IFilterStateLocator} implementation.
 * In short, search is done by example which is initialized cortege object.
 */
public class SortableFilteredDataProvider<T> extends SortableDataProvider<T> implements IFilterStateLocator<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SortableFilteredDataProvider.class);

    private T filter;

    private QueryParams params = new QueryParams(0, 20);

    private IService service;

    /**
     * SortableDataProvider
     *
     * @param service Entity service
     * @param aClass  class of entity to be provided
     */
    public SortableFilteredDataProvider(IService service, Class<T> aClass) {
        try {
            this.service = service;
            filter = aClass.newInstance();
        } catch (Exception e) {
            LOGGER.error("instantiation failed with {}", e);
            throw new WicketRuntimeException(e);
        }

    }

    /**
     * Callback used by the consumer of this data provider to wrap objects retrieved from
     * {@link #iterator(int, int)} with a model (usually a detachable one).
     *
     * @param object the object that needs to be wrapped
     * @return the model representation of the object
     */
    public IModel<T> model(T object) {
        return new DetachableEntityModel<>(object, service);
    }

    /**
     * Gets total number of items in the collection represented by the DataProvider
     *
     * @return total item count
     */
    public int size() {
        return (int) service.count(WicketObjects.cloneModel(filter));
    }

    /**
     * Gets an iterator for the subset of total data
     *
     * @param first first row of data
     * @param count minimum number of elements to retrieve
     * @return iterator capable of iterating over {first, first+count} items
     */
    public Iterator<? extends T> iterator(int first, int count) {
        params.setFirst(first).setCount(count);
        // Sorting parameters: fields and direction
        SortParam sp = getSort();
        if (sp != null) {
            params.setOrderField(sp.getProperty());
            params.setOrderAsc(sp.isAscending());
        }

        // It's guaranteed that returned object is of type of T
        // noinspection unchecked
        return service.find((T) WicketObjects.cloneModel(filter), params).iterator();
    }


    /********************************************************/
    /*************  IFilterStateLocator     *****************/
    /**
     * ****************************************************
     */

    public T getFilterState() {
        return filter;
    }

    public void setFilterState(T state) {
        this.filter = (T) state;
    }
}
