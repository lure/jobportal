package ru.shubert.jobportal.web.component;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.model.ResourceModel;

/**
 * Localized submit button. Beware of conflicts followed by {@link #onSubmit()} overriding:
 * it may clush with {@link org.apache.wicket.markup.html.form.Form#onSubmit()}  }
 * Requires &lt;input type="submit" wicket:id="cancel" &gt; tag in murkup
 */
public class SaveButton extends Button {

    public SaveButton() {
        super("save", new ResourceModel("save"));
        setDefaultFormProcessing(true);
    }

}
