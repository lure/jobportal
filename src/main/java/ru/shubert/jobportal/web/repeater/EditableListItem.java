package ru.shubert.jobportal.web.repeater;


import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

/**
 *  TODO it is unecessary complicate. getObject must be populated via reading getParent(Repeater.class).getModelObjcet.get(getIndex())
 *  This implementation already has setOutputMarkupId set to true
 *
 */
public class EditableListItem<T> extends WebMarkupContainer {
    private static final long serialVersionUID = 1L;

    /**
     * The index of the ListItem in the parent ListView
     */
    private int index;

    /**
     * A constructor which uses the index and the list provided to create a ListItem. This
     * constructor is the default one.
     *
     * @param index The index of the item
     * @param model The model object of the item
     */
    public EditableListItem(final int index, final IModel<T> model) {
        super(Integer.toString(index).intern(), model);
        this.index = index;
        setOutputMarkupId(true);
    }

    /**
     * Gets the index of the listItem in the parent listView.
     *
     * @return The index of this listItem in the parent listView
     */
    public final int getIndex() {
        return index;
    }

    /**
     * Gets model
     *
     * @return model
     */
    @SuppressWarnings("unchecked")
    public final IModel<T> getModel() {
        return (IModel<T>) getDefaultModel();
    }

    /**
     * Sets model
     *
     * @param model  to be set
     */
    public final void setModel(IModel<T> model) {
        setDefaultModel(model);
    }

    /**
     * Gets model object
     *
     * @return model object
     */
    @SuppressWarnings("unchecked")
    public final T getModelObject() {
        return (T) getDefaultModelObject();
    }

    /**
     * Sets model object
     *
     * @param object to be set
     */
    public final void setModelObject(T object) {
        setDefaultModelObject(object);
    }

    public void setIndex(int index) {
        this.index = index;
        IModel model = ((CompoundPropertyModel) getModel()).getChainedModel();
        ((EditableListItemModel) model).setIndex(index);
    }
}

