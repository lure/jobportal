package ru.shubert.jobportal.web.component;


import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

/**
 * Ajax modal confirmarion window
 */

public class ConfirmationWindow extends ModalWindow {
    ContentPanel content;


    public ConfirmationWindow(String id, final ConfirmationAnswer answer) {
        super(id);
        setTitle(new ResourceModel("yesnoconfirm"));
        setInitialHeight(100);
        setInitialWidth(300);
        setCookieName(id);

        content = new ContentPanel(getContentId(), answer);
        setContent(content);
    }



    public void setMessage(String message) {
        content.setMessage(message);
    }

    private class ContentPanel extends Panel {
        private MultiLineLabel label = new MultiLineLabel("message", "");

        private ContentPanel(String id, final ConfirmationAnswer answer) {
            super(id);
            addContent(answer);
        }

        protected void addContent(final ConfirmationAnswer answer) {
            Form form = new Form("yes-no-form");

            AjaxButton nobutton = new AjaxButton ("no-button") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    answer.setConfirmed(false);
                    close(target);
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                }
            };

            AjaxButton yesbutton = new AjaxButton("yes-button") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    answer.setConfirmed(true);
                    close(target);
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                }
            };

            form.add(label);
            form.add(nobutton);
            form.add(yesbutton);
            add(form);
        }

        void setMessage(String message) {
            label.setDefaultModelObject(message);
        }

    }
}
