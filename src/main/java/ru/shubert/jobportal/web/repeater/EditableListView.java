package ru.shubert.jobportal.web.repeater;


import org.apache.wicket.Component;
import org.apache.wicket.markup.repeater.AbstractRepeater;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.collections.ReadOnlyIterator;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Difference with {@link org.apache.wicket.markup.html.list.ListView} is:
 * <ol>
 * <li> operates on the {@link EditableListItem} which allows to change it's index.
 * <p/>
 * <li> raises visibility of {@link #newItem(int)} and {@link #populateItem(EditableListItem)} methods to public
 * hence now list may be used in ajax environment in scenarios such as
 * <pre><code>
 * ListItem item = {@link #newItem(int)};
 * {@link #populateItem(EditableListItem)};
 * view.add(item);
 * </code></pre>
 * <li> overrides {@link #onPopulate()} and {@link #renderIterator()}so it calls reflected get_child(int) in iteration
 *
 * @param <T> Model object type
 * @author Jonathan Locke
 * @author Juergen Donnerstag
 * @author Johan Compagner
 * @author Eelco Hillenius
 * @see org.apache.wicket.markup.html.list.ListView
 */
@SuppressWarnings({"UnusedDeclaration"})
public abstract class EditableListView<T> extends AbstractRepeater {
    private static final long serialVersionUID = 1L;

    /**
     * Index of the first item to show
     */
    private int firstIndex = 0;

    /**
     * If true, re-rendering the list view is more efficient if the window doesn't get changed at
     * all or if it gets scrolled (compared to paging). But if you modify the listView model object,
     * than you must manually call listView.removeAll() in order to rebuild the ListItems. If you
     * nest a ListView in a Form, ALWAYS set this property to true, as otherwise validation will not
     * work properly.
     */
    private boolean reuseItems = false;

    /**
     * Max number (not index) of items to show
     */
    private int viewSize = Integer.MAX_VALUE;


    /**
     * @param id    wicket:id
     * @param model with a list
     * @see org.apache.wicket.Component#Component(String, org.apache.wicket.model.IModel)
     */
    public EditableListView(final String id, final IModel<? extends List<? extends T>> model) {
        super(id, model);

        if (model == null) {
            throw new IllegalArgumentException(
                    "Null models are not allowed. If you have no model, you may prefer a Loop instead");
        }

        // A reasonable default for viewSize can not be determined right now,
        // because list items might be added or removed until ListView
        // gets rendered.
    }

    /**
     * @param id   See Component
     * @param list List to cast to Serializable
     * @see org.apache.wicket.Component#Component(String, IModel)
     */
    public EditableListView(final String id, final List<? extends T> list) {
        this(id, Model.ofList(list));
    }

    /**
     * Gets the list of items in the listView. This method is final because it is not designed to be
     * overridden. If it were allowed to be overridden, the values returned by getModelObject() and
     * getList() might not coincide.
     *
     * @return The list of items in this list view.
     */
    @SuppressWarnings("unchecked")
    public final List<? extends T> getList() {
        final List<? extends T> list = (List<? extends T>) getDefaultModelObject();
        if (list == null) {
            return Collections.emptyList();
        }
        return list;
    }

    /**
     * If true re-rendering the list view is more efficient if the windows doesn't get changed at
     * all or if it gets scrolled (compared to paging). But if you modify the listView model object,
     * than you must manually call listView.removeAll() in order to rebuild the ListItems. If you
     * nest a ListView in a Form, ALWAYS set this property to true, as otherwise validation will
     * not work properly.
     *
     * @return Whether to reuse items
     */
    public boolean getReuseItems() {
        return reuseItems;
    }

    /**
     * Get index of first cell in page. Default is: 0.
     *
     * @return Index of first cell in page. Default is: 0
     */
    public final int getStartIndex() {
        return firstIndex;
    }

    /**
     * Based on the model object's list size, firstIndex and view size, determine what the view size
     * really will be. E.g. default for viewSize is Integer.MAX_VALUE, if not set via setViewSize().
     * If the underlying list has 10 elements, the value returned by getViewSize() will be 10 if
     * startIndex = 0.
     *
     * @return The number of items to be populated and rendered.
     */
    public int getViewSize() {
        int size = viewSize;

        final Object modelObject = getDefaultModelObject();
        if (modelObject == null) {
            return size == Integer.MAX_VALUE ? 0 : size;
        }

        // Adjust view size to model object's list size
        final int modelSize = getList().size();
        if (firstIndex > modelSize) {
            return 0;
        }

        if ((size == Integer.MAX_VALUE) || ((firstIndex + size) > modelSize)) {
            size = modelSize - firstIndex;
        }

        // firstIndex + size must be smaller than Integer.MAX_VALUE
        if ((Integer.MAX_VALUE - size) < firstIndex) {
            throw new IllegalStateException(
                    "firstIndex + size must be smaller than Integer.MAX_VALUE");
        }

        return size;
    }

    /**
     * Sets the model as the provided list and removes all children, so that the next render will be
     * using the contents of the model.
     *
     * @param list The list for the new model. The list must implement {@link java.io.Serializable}.
     * @return This for chaining
     */
    public EditableListView<T> setList(List<? extends T> list) {
        setDefaultModel(Model.ofList(list));
        return this;
    }

    /**
     * If true re-rendering the list view is more efficient if the windows doesn't get changed at
     * all or if it gets scrolled (compared to paging). But if you modify the listView model object,
     * than you must manually call listView.removeAll() in order to rebuild the ListItems. If you
     * nest a ListView in a Form, ALLWAYS set this property to true, as otherwise validation will
     * not work properly.
     *
     * @param reuseItems Whether to reuse the child items.
     * @return this
     */
    public EditableListView<T> setReuseItems(boolean reuseItems) {
        this.reuseItems = reuseItems;
        return this;
    }

    /**
     * Set the index of the first item to render
     *
     * @param startIndex First index of model object's list to display
     * @return This
     */
    public EditableListView<T> setStartIndex(final int startIndex) {
        firstIndex = startIndex;

        if (firstIndex < 0) {
            firstIndex = 0;
        } else if (firstIndex > getList().size()) {
            firstIndex = 0;
        }

        return this;
    }

    /**
     * Define the maximum number of items to render. Default: render all.
     *
     * @param size Number of items to display
     * @return This
     */
    public EditableListView<T> setViewSize(final int size) {
        viewSize = size;

        if (viewSize < 0) {
            viewSize = Integer.MAX_VALUE;
        }

        return this;
    }

    /**
     * Subclasses may provide their own ListItemModel with extended functionality. The default
     * ListItemModel works fine with mostly static lists where index remains valid. In cases where
     * the underlying list changes a lot (many users using the application), it may not longer be
     * appropriate. In that case your own ListItemModel implementation should use an id (e.g. the
     * database' record id) to identify and load the list item model object.
     *
     * @param listViewModel The ListView's model
     * @param index         The list item index
     * @return The ListItemModel created
     */
    protected IModel<T> getListItemModel(final IModel<? extends List<T>> listViewModel,
                                         final int index) {
        return new CompoundPropertyModel<>(new EditableListItemModel<>(this, index));
    }

    /**
     * Create a new ListItem for list item at index.
     *
     * @param index of the item
     * @return ListItem
     */
    protected EditableListItem<T> newItem(final int index) {
        // we have to calculate new one, because that id is not the corespondance
        // between model and component input, but participate in building html id like "phones1", "phones2", etc
        Component c = get(size() - 1);
        int htmlId = (c != null) ? Integer.parseInt(get(size() - 1).getId()) + 1 : index;

        // and the index here is the relation between Entity.collection and FormComponent.model
        EditableListItem<T> item = new EditableListItem<>(htmlId, getListItemModel(getModel(), index));
        item.setIndex(index);
        return item;
    }

    /**
     * @see org.apache.wicket.markup.repeater.AbstractRepeater#onPopulate()
     */
    @SuppressWarnings("unchecked")
    @Override
    protected final void onPopulate() {
        // Get number of items to be displayed
        final int size = getViewSize();
        if (size > 0) {
            if (getReuseItems()) {
                // Remove all ListItems no longer required
                final int maxIndex = firstIndex + size;
                for (final Iterator<Component> iterator = iterator(); iterator.hasNext();) {
                    // Get next child component
                    final EditableListItem<T> child = (EditableListItem<T>)iterator.next();
                    if (child != null) {
                        final int index = child.getIndex();
                        if (index < firstIndex || index >= maxIndex) {
                            iterator.remove();
                        }
                    }
                }
            } else {
                // Automatically rebuild all ListItems before rendering the
                // list view
                removeAll();
            }

            boolean hasChildren = size() != 0;
            // Loop through the markup in this container for each item
            for (int i = 0; i < size; i++) {
                // Get index
                final int index = firstIndex + i;

                EditableListItem<T> item = null;
                if (hasChildren) {
                    item = (EditableListItem<T>) get(index);
                }
                if (item == null) {
                    // Create item for index
                    item = newItem(index);

                    // Add list item
                    add(item);

                    // Populate the list item
                    onBeginPopulateItem(item);
                    populateItem(item);
                }
            }
        } else {
            removeAll();
        }

    }

    /**
     * Comes handy for ready made ListView based components which must implement populateItem() but
     * you don't want to lose compile time error checking reminding the user to implement abstract
     * populateItem().
     *
     * @param item to be populated
     */
    protected void onBeginPopulateItem(final EditableListItem<T> item) {
    }

    /**
     * Populate a given item.
     * <p>
     * <b>be careful</b> to add any components to the list item. So, don't do:
     * <p/>
     * <code>add(new Label("foo", "bar")); </code>
     * <p> but: </p>
     * <code>item.add(new Label(&quot;foo&quot;, &quot;bar&quot;)); </code>
     *
     * @param item The item to populate
     */
    public abstract void populateItem(final EditableListItem<T> item);

    /**
     * @see org.apache.wicket.markup.repeater.AbstractRepeater#renderChild(org.apache.wicket.Component)
     */
    @Override
    protected final void renderChild(Component child) {
        renderItem((EditableListItem<?>) child);
    }


    /**
     * Render a single item.
     *
     * @param item The item to be rendered
     */
    protected void renderItem(final EditableListItem<?> item) {
        item.render();
    }

    /**
     * @see org.apache.wicket.markup.repeater.AbstractRepeater#renderIterator()
     */
    @Override
    protected Iterator<Component> renderIterator() {

        final int size = size();
        return new ReadOnlyIterator<Component>() {
            private int index = 0;

            public boolean hasNext() {
                return index < size;
            }

            public Component next() {
                int i = firstIndex + index;
                index++;
                return get(i);
            }
        };
    }


    @Override
    public Iterator<Component> iterator() {
        return super.iterator();
    }

    /**
     * Gets model
     *
     * @return model
     */

    public final IModel<? extends List<T>> getModel() {
        //noinspection unchecked
        return (IModel<? extends List<T>>) getDefaultModel();
    }

    /**
     * Sets model
     *
     * @param model to be set
     */
    public final void setModel(IModel<? extends List<T>> model) {
        setDefaultModel(model);
    }

    /**
     * Gets model object
     *
     * @return model object
     */
    public final List<T> getModelObject() {
        //noinspection unchecked
        return (List<T>) getDefaultModelObject();
    }

    /**
     * Sets model object
     *
     * @param object to be assign to the model
     */
    public final void setModelObject(List<T> object) {
        setDefaultModelObject(object);
    }



    /**
     * Appends an object to the view and underlay ModelObject collection
     *
     * @param object to be added
     * @return newly created Item
     */
    public EditableListItem<T> appendItem(final T object) {
        getModelObject().add(object);
        EditableListItem<T> item = newItem(getModelObject().size() - 1);
        populateItem(item);
        add(item);
        return item;
    }

    /**
     * removes an Item from the view and it's model object from the underlayed
     * model object collection.
     * Items following removed one get their index (not 'id') decreased by one
     * The index of removed item is determined by {@link EditableListItem#getIndex()}
     * @param item to be removed
     */
    public void removeItem(final EditableListItem<T> item) {
        for (int i = item.getIndex() + 1; i < size(); i++) {
            EditableListItem currentItem = ((EditableListItem) get(i));
            currentItem.setIndex(currentItem.getIndex() - 1);
        }

        getModelObject().remove(item.getModelObject());
        remove(item);
    }
}
