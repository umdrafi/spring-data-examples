/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package example.springdata.mongodb.people;

import org.reactivestreams.Publisher;
import org.springframework.data.mongodb.repository.InfiniteStream;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.RxJavaCrudRepository;

import rx.Observable;
import rx.Single;

/**
 * Repository interface to manage {@link Person} instances.
 *
 * @author Mark Paluch
 */
public interface RxJavaPersonRepository extends RxJavaCrudRepository<Person, String> {

	/**
	 * Inserts the given entity. Assumes the instance to be new to be able to apply insertion optimizations.
	 * <p>
	 * This method overrides {@link org.springframework.data.mongodb.repository.ReactiveMongoRepository#insert(Object)}
	 * and adopts RxJava's {@link Single} as return type.
	 *
	 * @param entity must not be {@literal null}.
	 * @return the saved entity
	 */
	Single<Person> insert(Object entity);

	/**
	 * Inserts the given a given entities. Assumes the instance to be new to be able to apply insertion optimizations.
	 * <p>
	 * This method overrides {@link org.springframework.data.mongodb.repository.ReactiveMongoRepository#insert(Publisher)}
	 * and adopts RxJava's {@link Observable} as return type.
	 *
	 * @param entities must not be {@literal null}.
	 * @return the saved entity
	 */
	Observable<Person> insert(Observable<Object> entities);

	/**
	 * Derived query selecting by {@code lastname}.
	 *
	 * @param lastname
	 * @return
	 */
	Observable<Person> findByLastname(String lastname);

	/**
	 * String query selecting one entity.
	 *
	 * @param lastname
	 * @return
	 */
	@Query("{ 'firstname': ?0, 'lastname': ?1}")
	Single<Person> findByFirstnameAndLastname(String firstname, String lastname);

	/**
	 * Use a tailable cursor to emit a stream of entities as new entities are written to the capped collection.
	 *
	 * @return
	 */
	@InfiniteStream
	Observable<Person> findWithTailableCursorBy();
}
