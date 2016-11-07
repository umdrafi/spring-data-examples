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

package example.springdata.cassandra.people;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.cassandra.core.ReactiveCassandraOperations;
import org.springframework.test.context.junit4.SpringRunner;

import example.springdata.cassandra.util.RequiresCassandraKeyspace;
import rx.Completable;
import rx.Observable;

/**
 * Integration test for {@link RxJava1PersonRepository} using RxJava1 types. Note that
 * {@link ReactiveCassandraOperations} is only available using Project Reactor types as the native Template API
 * implementation does not come in multiple reactive flavors.
 *
 * @author Mark Paluch
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RxJava1PersonRepositoryIntegrationTest {

	@ClassRule public final static RequiresCassandraKeyspace CASSANDRA_KEYSPACE = RequiresCassandraKeyspace.onLocalhost();

	@Autowired RxJava1PersonRepository repository;
	@Autowired ReactiveCassandraOperations operations;

	@Before
	public void setUp() throws Exception {

		Completable deleteAll = repository.deleteAll();

		Observable<Person> insert = repository.insert(Observable.just(new Person("Walter", "White"), //
				new Person("Skyler", "White"), //
				new Person("Saul", "Goodman"), //
				new Person("Jesse", "Pinkman")));//

		deleteAll.andThen(insert).toBlocking().last();
	}

	/**
	 * This sample performs a count, inserts data and performs a count again using reactive operator chaining.
	 */
	@Test
	public void shouldInsertAndCountData() throws Exception {

		CountDownLatch countDownLatch = new CountDownLatch(1);

		repository.count() //
				.doOnSuccess(System.out::println) //
				.toObservable() //
				.switchMap(count -> repository.save(Observable.just(new Person("Hank", "Schrader"), //
						new Person("Mike", "Ehrmantraut")))) //
				.last() //
				.toSingle() //
				.flatMap(v -> repository.count()) //
				.doOnSuccess(System.out::println) //
				.doAfterTerminate(countDownLatch::countDown) //
				.doOnError(throwable -> countDownLatch.countDown()) //
				.subscribe();

		countDownLatch.await();
	}

	/**
	 * Result set {@link com.datastax.driver.core.Row}s are converted to entities as they are emitted. Reactive pull and
	 * prefetch define the amount of fetched records.
	 */
	@Test
	public void shouldPerformConversionBeforeResultProcessing() throws Exception {

		CountDownLatch countDownLatch = new CountDownLatch(1);

		repository.findAll() //
				.doOnNext(System.out::println) //
				.doOnCompleted(countDownLatch::countDown) //
				.doOnError(throwable -> countDownLatch.countDown()) //
				.subscribe();

		countDownLatch.await();
	}

	/**
	 * Fetch data using query derivation.
	 */
	@Test
	public void shouldQueryDataWithQueryDerivation() {

		List<Person> whites = repository.findByLastname("White") //
				.toList() //
				.toBlocking() //
				.last();

		assertThat(whites).hasSize(2);
	}

	/**
	 * Fetch data using a string query.
	 */
	@Test
	public void shouldQueryDataWithStringQuery() {

		Person heisenberg = repository.findByFirstnameAndLastname("Walter", "White") //
				.toBlocking() //
				.value();

		assertThat(heisenberg).isNotNull();
	}
}
