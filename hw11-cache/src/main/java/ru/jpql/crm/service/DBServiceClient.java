package ru.jpql.crm.service;

import ru.jpql.crm.model.Client;

import java.util.List;
import java.util.Optional;

public interface DBServiceClient {

    Client saveClient(Client client);

    Optional<Client> getClient(long id);

    Optional<Client> getClientCached(long id);

    List<Client> findAll();
}
