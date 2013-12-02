package ru.shubert.jobportal.web.component;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.collections.MicroMap;
import org.apache.wicket.util.string.interpolator.MapVariableInterpolator;
import ru.shubert.jobportal.strategy.ORMHelper;
import ru.shubert.jobportal.web.HomePage;
import ru.shubert.jobportal.web.proto.BasePage;

import java.util.Map;

/**
 * Common <code>Cancel</code> template with localized caption and
 * "go back" behaviour.
 * Requires &lt;input type="submit" wicket:id="cancel" &gt; tag in murkup
 * Does not leads to {@link org.apache.wicket.markup.html.form.Form#onSubmit()} call as setDefaultFormProcessing
 * is set to false
 */
public class CancelButton extends Button {

    private static final long serialVersionUID = 1L;

    public CancelButton() {
        super("cancel", new ResourceModel("cancel"));
        setDefaultFormProcessing(false);
    }

    @Override
    public void onSubmit() {
        // Is it really wicket way? TODO revisit message interpolation
        String info = getString("status.cancel");
        Map map = new MicroMap<>("name",  getString(ORMHelper.getClass(getForm().getModelObject()).getName()));
        getSession().info(MapVariableInterpolator.interpolate(info, map));
        setResponsePage(HomePage.class);

        /*
        Page prevPage = ((BasePage) getPage()).getPreviousPage();
        if (prevPage != null) {
            setResponsePage(((BasePage) getPage()).getPreviousPage());
        } else {
            setResponsePage(HomePage.class);
        }
        */
    }
}