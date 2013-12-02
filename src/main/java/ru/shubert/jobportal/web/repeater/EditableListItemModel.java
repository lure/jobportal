package ru.shubert.jobportal.web.repeater;

import org.apache.wicket.model.IModel;

/**
 *
 *
 *
 */
public class EditableListItemModel<T> implements IModel<T> {
    private static final long serialVersionUID = 1L;

    /** The ListView itself */
    private final EditableListView<T> listView;

    /** The list item's index */
    private int index;

    /**
     * Construct
     *
     * @param listView
     *            The ListView
     * @param index
     *            The index of this model
     */
    public EditableListItemModel(final EditableListView<T> listView, final int index)
    {
        this.listView = listView;
        this.index = index;
    }

    /**
     * @see org.apache.wicket.model.IModel#getObject()
     */
    public T getObject()
    {
        return listView.getModelObject().get(index);
    }

    /**
     * @see org.apache.wicket.model.IModel#setObject(java.lang.Object)
     */
    public void setObject(T object)
    {
        listView.getModelObject().set(index, object);
    }

    /**
     * @see org.apache.wicket.model.IDetachable#detach()
     */
    public void detach()
    {
        // Do nothing. ListView will detach its own model object.
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
