package ru.shubert.jobportal.web.component;


import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.jetbrains.annotations.NotNull;

/**
 * Modal window for panel uses. Has predefined dimensions and provides abstract callback method for OnClose event
 * {@link #isConfirmed()} returns false if was closed with cancel or close button.
 * Must be supplied with a {@link org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.PageCreator} instance.
 */
abstract public class ComplexModalWindow<T> extends ModalWindow {

    private T object;

    boolean confirmed = false;

    /**
     * Creates new modal window with predefined width of 800 and height of 800
     * and sets {@link WindowClosedCallback} to abstract method
     *
     * @param id wicket:id
     */
    public ComplexModalWindow(String id) {
        super(id);
        setMinimalHeight(500);
        setMinimalWidth(800);

        setWindowClosedCallback(new WindowClosedCallback() {
            public void onClose(AjaxRequestTarget target) {
                ComplexModalWindow.this.onClose(target);
            }
        });
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setCookieName(getPage().getClass().getName() + "-modal");
    }

    /**
     * Must be implemented in order to react on window close.
     *
     * @param target ajax target of request
     */
    abstract public void onClose(AjaxRequestTarget target);

    public ComplexModalWindow setResultObject(@NotNull T object) {
        this.object = object;
        return this;
    }

    public ComplexModalWindow setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
        return this;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public T getResultObject() {
        return object;
    }

    public void clearState() {
        confirmed = false;
        object = null;
    }
}
