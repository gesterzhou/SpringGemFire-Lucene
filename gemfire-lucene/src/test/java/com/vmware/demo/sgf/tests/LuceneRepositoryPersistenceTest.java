/*
 * Copyright 2012 VMWare.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vmware.demo.sgf.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.vmware.demo.sgf.lucene.LuceneGemFireRepository;
import com.vmware.demo.sgf.lucene.LuceneIndexRepoFactory;
import com.vmware.demo.sgf.lucene.domain.SearchableGemFireEntity;
import com.vmware.demo.sgf.tests.domain.Person;
import com.vmware.demo.sgf.tests.domain.PersonKey;

/**
 * @author Lyndon Adams
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:app-context.xml"})
public class LuceneRepositoryPersistenceTest {

	static String[] surnames = {"Adams", "Smith", "Rogers", "Baker", "Bennett", "Jones"};
	static String[] firstnames = {"Lyndon", "India", "Jennie", "Saffron", "David", "Robert"};


	@Test
	public void saveSingleDocument() {
		try {
			LuceneGemFireRepository repo = LuceneIndexRepoFactory.getInstance();

			PersonKey key = new PersonKey(100, "Adams");
			Person value = new Person(key.getId(), "Lyndon", "Adams", new Date(1970, 4, 30));

			repo.save( new SearchableGemFireEntity(key, value) );		

		} catch(IOException e){
			throw new RuntimeException( e);
		}
	}

	@Test
	public void saveMultipleDocuments() {
		try {
			LuceneGemFireRepository repo = LuceneIndexRepoFactory.getInstance();

			ArrayList<SearchableGemFireEntity> persons = new ArrayList<SearchableGemFireEntity>();
			int id = 1000;

			for(int k=0;k<surnames.length; k++){
				for(int i=0; i<firstnames.length; i++ ){
					PersonKey key = new PersonKey(++id, surnames[k]);
					Person value = new Person(id, firstnames[i], surnames[k], new Date(1970, id%12, id %30));
					persons.add( new SearchableGemFireEntity(key, value) );				
				}
			}
			repo.save( persons );
		} catch(IOException e){
			throw new RuntimeException( e);
		}	
	}

	@AfterClass
	public static void closeRepository(){
		try {
			LuceneIndexRepoFactory.getInstance().close();
		} catch(IOException e){
			throw new RuntimeException( e);
		}
	}
}
