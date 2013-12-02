package ru.shubert.jobportal.config;

import org.apache.wicket.protocol.http.WicketFilter;
import org.springframework.orm.hibernate4.support.OpenSessionInViewFilter;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.*;
import java.util.EnumSet;

/**
 * <p>
 * This class is an web.xml replacement example. Such kind of configuration was introduced by Servlet 3.0 API.
 * There are no magic here, Spring just have class in a jar's meta-inf/service folder. Being booting this class
 * looking for any WebApplicationInitializer instances and call it's onStartup.<br />
 * Dig {@link WebApplicationInitializer} documentation for details.
 * </p>
 *
 * <p><b>NOTE:</b><ol></p>
 * <LI>Jetty prior 8.1.4 contains a bug with context initialization that leads to inproper initializatin.
 *      So if there are any time-requiring tasks they maybe interrupted by jetty thread.</LI>
 * <LI>Wicket Filter checks it's path on it's own quering init params and web.xml
 *      and raises exception if nothing found </LI>
 * <LI>OSIV filter must be matched strictly BEFORE wicket filter. So use matchedAfter parameter wisely</LI>
 * </ol>
 *
 * <p>Initialization sequence</p>
 * <ol>
 *  <li>Servlet 3 container finds all classes implementing <a href="http://download.oracle.com/javaee/6/api/javax/servlet/ServletContainerInitializer.html">ServletContainerInitializer</a></li>
 *  <li>Container executes a Spring implementation <a href="http://static.springsource.org/spring/docs/3.1.0.RC1/javadoc-api/org/springframework/web/SpringServletContainerInitializer.html">SpringServletContainerInitializer</a></li>
 *  <li>SpringServletContainerInitializer finds all classes implementing <a href="http://static.springsource.org/spring/docs/3.1.0.RC1/javadoc-api/org/springframework/web/WebApplicationInitializer.html">WebApplicationInitializer</a></li>
 *  <li>Spring executes our root configuration implementation <a href="https://github.com/Pyrolistical/xml-free-spring/blob/master/src/main/java/com/github/pyrolistical/config/WebApp.java">WebApp</a></li>
 *  <li>Our root configuration create a new Spring context <a href="http://static.springsource.org/spring/docs/3.1.0.RC1/javadoc-api/org/springframework/web/context/support/AnnotationConfigWebApplicationContext.html">AnnotationConfigWebApplicationContext</a></li>
 *  <li>The context finds all classes with the <a href="http://static.springsource.org/spring/docs/3.1.0.RC1/javadoc-api/org/springframework/context/annotation/Configuration.html">@Configuration</a> annotation</li>
 *  <li>The context executes our controller configuration <a href="https://github.com/Pyrolistical/xml-free-spring/blob/master/src/main/java/com/github/pyrolistical/config/ControllerConfiguration.java">ControllerConfiguration</a></li>
 *  <li>Our controller configuration finds all classes with the <a href="http://static.springsource.org/spring/docs/3.1.0.RC1/javadoc-api/org/springframework/stereotype/Controller.html">@Controller</a> annotation</li>
 *  <li>The configuration creates our controller <a href="https://github.com/Pyrolistical/xml-free-spring/blob/master/src/main/java/com/github/pyrolistical/controller/RootController.java">RootController</a></li>
 *  <li>Finally the context we created in step 5 is fed into the Spring Servlet <a href="http://static.springsource.org/spring/docs/3.1.0.RC1/javadoc-api/org/springframework/web/servlet/DispatcherServlet.html">DispatcherServlet</a></li>
 </ol>
 *
 * @see <a href="http://docs.oracle.com/javase/6/docs/technotes/guides/jar/jar.html#Service%20Provider">SPI</a>
 */
public class WebConfig implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext sc) throws ServletException {
        sc.setAttribute("org.mortbay.jetty.servlet.SessionURL", "none");

        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        //context.scan("ru.shubert.jobportal.config");
        //context.getEnvironment().setDefaultProfiles("embedded");
        context.register(AppConfig.class);
        context.refresh();
        sc.addListener(new ContextLoaderListener(context));


        // charset Filter enforcer
        FilterRegistration.Dynamic charsetFilter =  sc.addFilter("charsetFilter", CharacterEncodingFilter.class);
        charsetFilter.setInitParameter("encoding","UTF-8");
        charsetFilter.setInitParameter("forceEncoding","true");
        charsetFilter.addMappingForUrlPatterns(null, false, "/*");


        // Open Session in view
        FilterRegistration.Dynamic osivFilter = sc.addFilter("osiv", OpenSessionInViewFilter.class);
        osivFilter.addMappingForUrlPatterns(null, false, "/*");

        // Wicket itself
        FilterRegistration.Dynamic wicketFilter = sc.addFilter("wicketFilter", WicketFilter.class);
        wicketFilter.setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, "/*");
        wicketFilter.setInitParameter("applicationFactoryClassName","org.apache.wicket.spring.SpringWebApplicationFactory");
        wicketFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR), true, "/*");

        ServletRegistration.Dynamic h2Servlet = sc.addServlet("H2Console", org.h2.server.web.WebServlet.class);
        h2Servlet.setInitParameter("webAllowOthers", "true");
        h2Servlet.setLoadOnStartup(2);
        h2Servlet.addMapping("/admin/h2/*");

    }
}
