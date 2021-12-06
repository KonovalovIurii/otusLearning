package ru.otus.webserver.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.webserver.services.TemplateProcessor;
import ru.otus.webserver.services.UserAuthService;
import ru.otus.webserver.servlet.AuthorizationFilter;
import ru.otus.webserver.servlet.ClientsLoginServlet;
import ru.otus.webserver.servlet.ClientsServlet;

import java.util.Arrays;

public class UserWebServerHomeWork implements UsersWebServer{
    private static final String START_PAGE_NAME = "indexhomework.html";
    private static final String COMMON_RESOURCES_DIR = "static";

    private final DBServiceClient serviceClient;

    protected final TemplateProcessor templateProcessor;
    private final Server server;

    private final UserAuthService adminAuthServiceImpl;


    public UserWebServerHomeWork(int port,UserAuthService adminAuthServiceImpl,
            DBServiceClient serviceClient, TemplateProcessor templateProcessor ) {

        this.serviceClient = serviceClient;
        this.templateProcessor = templateProcessor;
        this.server = new Server(port);
        this.adminAuthServiceImpl = adminAuthServiceImpl;
    }

    @Override
    public void start() throws Exception {
        if (server.getHandlers().length == 0) {
            initContext();
        }
        server.start();
    }

    @Override
    public void join() throws Exception {
        server.join();
    }

    @Override
    public void stop() throws Exception {
        server.stop();
    }

    private Server initContext(){
        ResourceHandler resourceHandler = createResourceHandler();
        ServletContextHandler servletContextHandler = createServletContextHandler();

        HandlerList handlers = new HandlerList();
        handlers.addHandler(resourceHandler);
        handlers.addHandler(applySecurity(servletContextHandler, "/clients"));


        server.setHandler(handlers);
        return server;
    }
    protected Handler applySecurity(ServletContextHandler servletContextHandler, String... paths) {
        servletContextHandler.addServlet(new ServletHolder(new ClientsLoginServlet(templateProcessor, adminAuthServiceImpl)), "/login");
        AuthorizationFilter authorizationFilter = new AuthorizationFilter();
        Arrays.stream(paths).forEachOrdered(path -> servletContextHandler.addFilter(new FilterHolder(authorizationFilter), path, null));
        return servletContextHandler;
    }

    private ResourceHandler createResourceHandler() {
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(false);
        resourceHandler.setWelcomeFiles(new String[]{START_PAGE_NAME});
        resourceHandler.setResourceBase(/*FileSystemHelper.localFileNameOrResourceNameToFullPath(*/COMMON_RESOURCES_DIR/*)*/);
        return resourceHandler;
    }

    private ServletContextHandler createServletContextHandler() {
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.addServlet(new ServletHolder(new ClientsServlet(templateProcessor, serviceClient)), "/clients");

        return servletContextHandler;
    }
}
