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

import static org.junit.Assert.assertFalse;

import java.io.IOException;
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
import com.vmware.demo.sgf.lucene.impl.LuceneGemFireRepositoryImpl;
import com.vmware.demo.sgf.tests.domain.Person;
import com.vmware.demo.sgf.tests.domain.PersonKey;

/**
 * @author Lyndon Adams
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:app-context.xml"})
public class LuceneRepositoryDeleteTest {
	
	static LuceneGemFireRepository repo ;
	
	
	@Test
	public void deleteSingleRecord(){
		try {
			repo = LuceneIndexRepoFactory.getInstance();

			long total = repo.count();
			System.out.println("1:Total: " + total);

			PersonKey key = new PersonKey(100, "Adams");
			Person value = new Person(key.getId(), "Lyndon", "Adams", new Date(1970, 4, 30));

			repo.save( new SearchableGemFireEntity(key, value) );	

			total = repo.count();
			System.out.println("2:Total: " + total);

			repo.delete( value );
			
			total = total - repo.count();
			
			System.out.println("3:Total: " + total);
			assertFalse( ( total == 0 ) ? true : false );
			
		} catch(IOException e){
			throw new RuntimeException( e);
		}	
	}

	@Test
	public void deleteSetOfRecords(){
		
	}
	
	@Test
	public void deleteAllRecords(){
		repo.deleteAll();
	}
	
	@AfterClass
	public static void closeRepository(){
		repo.close();
	}

}
