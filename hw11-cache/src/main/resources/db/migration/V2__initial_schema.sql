 -- Для @GeneratedValue(strategy = GenerationType.IDENTITY)
 /*
 create table client
 (
     id   bigserial not null primary key,
     name varchar(50)
 );
  */

/* drop table client;*/
  -- Для @GeneratedValue(strategy = GenerationType.SEQUENCE)

  create sequence hibernate_sequence start with 1 increment by 1;


  create table address
  (
      id   bigint not null primary key,
      street varchar(50)
  );

  create table client
  (
      id   bigint not null primary key,
      name varchar(50),
      address_id bigint,
      FOREIGN KEY (address_id) REFERENCES address (id)
  );

  create table phone
  (
      id   bigint not null primary key,
      client_id bigint,
      number varchar(50),
      FOREIGN KEY (client_id) REFERENCES client (id)
  );