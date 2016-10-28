# Spring Data MongoDB 2.0 - Reactive examples

This project contains samples of reactive data access features with Spring Data (MongoDB).

## Prerequisites

MongoDB requires the Reactive Streams driver to provide reactive data access. 
The Reactive Streams driver maintains its own connections. Using Spring Data MongoDB Reactive support 
together with blocking Spring Data MongoDB data access will open multiple connections to your MongoDB servers.

This example requires specific dependencies in your project:

```xml
<dependency>
    <groupId>org.mongodb</groupId>
    <artifactId>mongodb-driver-reactivestreams</artifactId>
    <version>1.2.0</version>
</dependency>

<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-commons</artifactId>
    <version>2.0.0.DATACMNS-836-SNAPSHOT</version>
</dependency>

<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-mongodb</artifactId>
    <version>2.0.0.DATAMONGO-1444-SNAPSHOT</version>
</dependency>
```

## Reactive Template API usage with `ReactiveMongoTemplate` 

The main reactive Template API class is `ReactiveMongoTemplate`, ideally used through its interface `ReactiveMongoOperations`. It defines a basic set of reactive data access operations using [Project Reactor](http://projectreactor.io) `Mono` and `Flux` reactive types.

```java
template.insertAll(Flux.just(new Person("Walter", "White"),
				new Person("Skyler", "White"),
				new Person("Saul", "Goodman"),
				new Person("Jesse", "Pinkman")));
				
Flux<Person> flux = template.find(Query.query(Criteria.where("lastname").is("White")), Person.class);
```

The test cases in `ReactiveMongoTemplateIntegrationTest` show basic Template API usage. 
Reactive data access reads and converts individual elements while processing the stream.


## Reactive Repository support
 
Spring Data MongoDB provides reactive repository support with Project Reactor and RxJava 1 reactive types. The reactive API supports reactive type conversion between reactive types.

```java
public interface ReactivePersonRepository extends ReactiveMongoRepository<Person, String> {

	Flux<Person> findByLastname(String lastname);

	@Query("{ 'firstname': ?0, 'lastname': ?1}")
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

	@Query("{ 'firstname': ?0, 'lastname': ?1}")
	Single<Person> findByFirstnameAndLastname(String firstname, String lastname);

	@InfiniteStream // Use a tailable cursor
	Observable<Person> findWithTailableCursorBy();
}
```