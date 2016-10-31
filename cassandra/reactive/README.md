# Spring Data Cassandra 2.0 - Reactive examples

This project contains samples of reactive data access features with Spring Data (MongoDB).

## Prerequisites

This example requires specific dependencies in your project:

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-commons</artifactId>
    <version>2.0.0.DATACMNS-836-SNAPSHOT</version>
</dependency>

<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-cql</artifactId>
    <version>2.0.0.DATACASS-292-SNAPSHOT</version>
</dependency>

<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-cassandra</artifactId>
    <version>2.0.0.DATACASS-292-SNAPSHOT</version>
</dependency>
```

## Reactive Template API usage with `ReactiveCassandraTemplate` 

The main reactive Template API class is `ReactiveCassandraTemplate`, ideally used through its interface `ReactiveCassandraOperations`. It defines a basic set of reactive data access operations using [Project Reactor](http://projectreactor.io) `Mono` and `Flux` reactive types.

```java
template.insert(Flux.just(new Person("Walter", "White"),
				new Person("Skyler", "White"),
				new Person("Saul", "Goodman"),
				new Person("Jesse", "Pinkman")));
				
Flux<Person> flux = template.select(select()
                                         .from("person")
                                         .where(eq("lastname", "White")), Person.class);
```

The test cases in `ReactiveCassandraTemplateIntegrationTest` show basic Template API usage. 
Reactive data access reads and converts individual elements while processing the stream.


## Reactive Repository support
 
Spring Data Cassandra provides reactive repository support with Project Reactor and RxJava 1 reactive types. The reactive API supports reactive type conversion between reactive types.

```java
public interface ReactivePersonRepository extends ReactiveCassandraRepository<Person, String> {

	Flux<Person> findByLastname(String lastname);

	@Query("SELECT * FROM person WHERE firstname = ?0 and lastname  = ?1")
	Mono<Person> findByFirstnameAndLastname(String firstname, String lastname);

	
	@InfiniteStream // Use a tailable cursor
	Flux<Person> findWithTailableCursorBy();
}
```

```java
public interface RxJavaPersonRepository extends RxJavaCrudRepository<Person, String> {

    // Allow implementation method overrides and reactive type conversion
	Observable<Person> insert(Observable<Object> entities);

	Observable<Person> findByLastname(String lastname);

	@Query("SELECT * FROM person WHERE firstname = ?0 and lastname  = ?1")
	Single<Person> findByFirstnameAndLastname(String firstname, String lastname);

	@InfiniteStream // Use a tailable cursor
	Observable<Person> findWithTailableCursorBy();
}
```