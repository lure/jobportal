package ru.shubert.jobportal.web.panel;


import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.jetbrains.annotations.Nullable;
import ru.shubert.jobportal.web.repeater.EditableListItem;
import ru.shubert.jobportal.web.repeater.EditableListView;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Base class for editable panel list.
 * <ul>
 * <li>populate panel with {@link #onPopulatePanel(ru.shubert.jobportal.web.repeater.EditableListItem)}</li>
 * <li>reimplement onClick if necessary for a new panel creation in
 * {@link #onAddPanelClick(org.apache.wicket.ajax.AjaxRequestTarget, org.apache.wicket.markup.html.form.Form)}.
 * By default it creates new block(consider to add abstract method which returns tag name)
 * element row and calls onPopulateItem</li>
 * </ul>
 * <p/>
 * Please use {@link #newRemoveItemButton(String, ru.shubert.jobportal.web.repeater.EditableListItem)} if in need of item remove button
 * You have access to Container, ListView and Button vie respectful getters.
 * Hierarchy: container->(ListView->(n)Panel + Button)
 */
public abstract class AbstractEditablePanel<T> extends FormComponentPanel<Collection<T>> {
    public static String REMOVEITEMJSCRIPT = "var item=Wicket.$('%s'); if (item.parentNode.tagName == 'TBODY')" +
            "{item.parentNode.parentNode.removeChild(item.parentNode);} else {item.parentNode.removeChild(item);}";

    public static String ADDITEMJSCRIPT = "var item=document.createElement('%s'); " +
                                          "item.id='%s'; Wicket.$('%s').appendChild(item);";
    public static String ADDITEMPOSTSCRIPT = "$('#%s .date').datepicker();";

    //http://www.onkarjoshi.com/blog/188/hibernateexception-a-collection-with-cascade-all-delete-orphan-was-no-longer-referenced-by-the-owning-entity-instance/
    protected List<T> items = new LinkedList<>();

    private EditableListView<T> view;

    private WebMarkupContainer container;

    //private AjaxButton addButton;
    private AjaxSubmitLink addButton;

    public AbstractEditablePanel(String id) {
        this(id, null);
    }

    public AbstractEditablePanel(String id, final @Nullable IModel<Collection
            <T>> listIModel) {
        super(id, listIModel);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        if (getModelObject() == null) {
            throw new WicketRuntimeException("AbstractEditablePanel was supplied with a null model object");
        }

        items.addAll(getModelObject());

        container = new WebMarkupContainer("panel-container");
        container.setOutputMarkupId(true);

        view = new EditableListView<T>("panel", items) {
            @Override
            public void populateItem(EditableListItem<T> listItem) {
                AbstractEditablePanel.this.onPopulatePanel(listItem);
            }
        };
        view.setReuseItems(true);
        container.add(view);


        addButton = new AjaxSubmitLink ("add-panel") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                AbstractEditablePanel.this.onAddPanelClick(target, form);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {  }
        };
        addButton.setDefaultFormProcessing(false);

        add(container, addButton);
    }

    protected abstract void onPopulatePanel(final EditableListItem<T> listItem);

    protected void onAddPanelClick(final AjaxRequestTarget target, final Form<?> form) {
        EditableListItem<T> item = getView().appendItem(newObject());
        target.prependJavaScript(String.format(ADDITEMJSCRIPT, getChildWrapperTagName(),
                                                item.getMarkupId(), getContainer().getMarkupId()));
        target.appendJavaScript(String.format(ADDITEMPOSTSCRIPT, item.getMarkupId()));
        target.add(item);
    }

    protected AjaxButton newRemoveItemButton(String id, final EditableListItem<T> listItem) {
        AjaxButton button = new AjaxButton(id, new ResourceModel("remove-panel")) {
            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
               error("newRemoveItemButton failed"); }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                getView().removeItem(listItem);
                target.prependJavaScript(String.format(REMOVEITEMJSCRIPT, listItem.getMarkupId()));
            }
        };
        button.setDefaultFormProcessing(false);
        return button;
    }

    protected AjaxSubmitLink newRemoveItemLink(String id, final EditableListItem<T> listItem) {
        AjaxSubmitLink link = new AjaxSubmitLink(id ) {
            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {error("newRemoveItemLink failed"); }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                getView().removeItem(listItem);
                target. prependJavaScript(String.format(REMOVEITEMJSCRIPT, listItem.getMarkupId()));
            }
        };
        link.setDefaultFormProcessing(false);
        return link;
    }

    /**
     * returns repeater with a panels
     *
     * @return repeater
     */
    public EditableListView<T> getView() {
        return view;
    }

    /**
     * returns container enveloping all the panels
     *
     * @return top most container
     */
    public WebMarkupContainer getContainer() {
        return container;
    }

    /**
     * returns 'add new panel' button
     *
     * @return add button
     */
    @SuppressWarnings({"UnusedDeclaration"})
    public AjaxSubmitLink getAddButton() {
        return addButton;
    }

    @Override
    public void updateModel() {
        getModelObject().retainAll(items);

        Iterator<T> iterator = items.iterator();
        while (iterator.hasNext()) {
            T p = iterator.next();
            if (!getModelObject().contains(p)) {
                getModelObject().add(p);
            }
            iterator.remove();
        }

    }

    /**
     * Is called from the Add button handler and returns new object. This object may be transferred to some
     * modal window where his fields may be set.
     *
     * @return newly created object.
     */
    protected abstract T newObject();

    /**
     * Depends on markup in your template. Usually it convenient to use 'div' but html5 recommend
     * 'block' instead
     *
     * @return child wrapper tag name
     */
    protected String getChildWrapperTagName() {
        return "block";
    }

}