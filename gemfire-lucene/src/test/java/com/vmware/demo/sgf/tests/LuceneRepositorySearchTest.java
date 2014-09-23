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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.vmware.demo.sgf.lucene.LuceneGemFireRepository;
import com.vmware.demo.sgf.lucene.LuceneIndexRepoFactory;
import com.vmware.demo.sgf.lucene.domain.SearchType;
import com.vmware.demo.sgf.lucene.domain.SearcheCriteria;
import com.vmware.demo.sgf.lucene.impl.LuceneGemFireRepositoryImpl;

/**
 * @author Lyndon Adams
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:app-context.xml"})
public class LuceneRepositorySearchTest {

	static String[] surnames = {"Adams", "Smith", "Rogers", "Baker", "Bennett", "Jones"};
	static String[] firstnames = {"Lyndon", "India", "Jennie", "Saffron", "David", "Robert"};

	@Test
	public void findSingleDocument(){
		try {	
			LuceneGemFireRepository repo = LuceneIndexRepoFactory.getInstance();

			String searchField = "surname";
			String searchText = "Rogers";

			Object obj = repo.findOne( new SearcheCriteria( searchField, searchText, SearchType.UNIQUE, "/Instrument") );
			
			assertFalse( ( obj == null) ? false : false );


		} catch(IOException e){
			throw new RuntimeException( e);
		}
	}


	@Test
	public void findAllDocumentsBySurname(){
		try {
			LuceneGemFireRepository repo = LuceneIndexRepoFactory.getInstance();

			List<SearcheCriteria> sc = new ArrayList<SearcheCriteria>();

			String searchField = "surname";
			for(String surname : surnames){
				sc.add( new SearcheCriteria( searchField, surname, SearchType.UNIQUE, "/Instrument") );
			}

			List obj = (List) repo.findAll( sc );

			assertFalse( ( obj == null) ? true : false );

		} catch(IOException e){
			throw new RuntimeException( e);
		}
	}
	
	@Test 
	public void findAllDocuments(){
		try {
			LuceneGemFireRepository repo = LuceneIndexRepoFactory.getInstance();
			
			List obj = (List) repo.findAll( );
			
			assertFalse( ( obj == null) ? true : false );
			
		} catch(IOException e){
			throw new RuntimeException( e);
		}
		
	}
}
