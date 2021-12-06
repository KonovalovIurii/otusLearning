package ru.otus.webserver;

import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.core.repository.HibernateUtils;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DbServiceClientImpl;
import ru.otus.demo.DbServiceDemo;
import ru.otus.webserver.server.UserWebServerHomeWork;
import ru.otus.webserver.server.UsersWebServer;
import ru.otus.webserver.services.AdminAuthServiceImpl;
import ru.otus.webserver.services.TemplateProcessor;
import ru.otus.webserver.services.TemplateProcessorImpl;
import ru.otus.webserver.services.UserAuthService;

/*
// Стартовая страница
    http://localhost:8080

    логин: admin храниться в файле  admin.properties
    пароль: 1111 храниться в файле pass.txt (salt храниться в salt.txt - для генерации используется класс GenPassword)

   // Страница пользователей
   http://localhost:8080/clients
*/
public class WebServerHomeWork {
    private static final int WEB_SERVER_PORT = 8080;
    private static final String TEMPLATES_DIR = "/templates/";
    private static final Logger log = LoggerFactory.getLogger(DbServiceDemo.class);
    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";
    private static final String REALM_NAME = "AnyRealm";

    public  static final String HASH_LOGIN_SERVICE_ADMIN_LOGIN ="admin.properties";
    public static final String HASH_LOGIN_SERVICE_SALT = "salt.txt";
    public static final String HASH_LOGIN_SERVICE_ADMIN_PASS = "pass.txt";

    public static void main(String[] args) throws Exception {
        //иницируем сервис для взаимодействия с бд
        var dbServiceClients = initDbService();
        //добавим обработку шаблонов
        TemplateProcessor templateProcessor = new TemplateProcessorImpl(TEMPLATES_DIR);
        //авторизация
        UserAuthService authService = new AdminAuthServiceImpl();
        //создадим сервер
        UsersWebServer usersWebServer = new UserWebServerHomeWork(WEB_SERVER_PORT,
                authService, dbServiceClients, templateProcessor);

        //запуск
        usersWebServer.start();

        // блокируем поток
        usersWebServer.join();
    }


    public static DbServiceClientImpl initDbService() {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");
        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();
        var sessionFactory = HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);
        var transactionManager = new TransactionManagerHibernate(sessionFactory);
        var clientTemplate = new DataTemplateHibernate<>(Client.class);
        var dbServiceClient = new DbServiceClientImpl(transactionManager, clientTemplate);
        return dbServiceClient;
    }
}
