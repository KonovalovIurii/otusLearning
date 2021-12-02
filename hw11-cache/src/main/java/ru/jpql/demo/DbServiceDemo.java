package ru.jpql.demo;

import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.jpql.core.repository.DataTemplateHibernate;
import ru.jpql.core.repository.HibernateUtils;
import ru.jpql.core.sessionmanager.TransactionManagerHibernate;
import ru.jpql.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.jpql.crm.model.Address;
import ru.jpql.crm.model.Client;
import ru.jpql.crm.model.Phone;
import ru.jpql.crm.service.DbServiceClientImpl;

import java.util.ArrayList;

public class DbServiceDemo {

    private static final Logger log = LoggerFactory.getLogger(DbServiceDemo.class);

    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

    public static void main(String[] args) {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        var sessionFactory = HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);

        var transactionManager = new TransactionManagerHibernate(sessionFactory);

        var clientTemplate = new DataTemplateHibernate<>(Client.class);

        var dbServiceClient = new DbServiceClientImpl(transactionManager, clientTemplate);

        //список id клиентов для прогона

        var clientsId = new ArrayList<Long>();
        for (long i = 1; i <= 50; i++) {
            //Create Frist client
            var address = new Address("St. Krasnaia ap." + i);

            var clientFirst = new Client("dbServiceFirst" + i, address);
            clientFirst.addPhone(new Phone("+790199991" + i));
            clientFirst.addPhone(new Phone("+790299992" + i));
            var ids = dbServiceClient.saveClient(clientFirst).getId();
            clientsId.add(ids);
        }

        log.info("Get clients without cache");
        clientsId.forEach(id -> log.info(dbServiceClient.getClient(id).get().getName()));
        log.info("Get clients without cache  - done");

        log.info("Get clients with cache");
        clientsId.forEach(id -> log.info(dbServiceClient.getClientCached(id).get().getName()));
        log.info("Get clients with cache  - done");

        //Start GC
        System.gc();
        log.info("Get clients with cache after gc");
        clientsId.forEach(id -> log.info(dbServiceClient.getClientCached(id).get().getName()));
        log.info("Get clients with cache after gc - done");
    }
}
