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
package com.vmware.example.lucene;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.Pool;
import com.gemstone.gemfire.cache.client.PoolManager;
import com.gemstone.gemfire.cache.execute.FunctionService;
import com.gemstone.gemfire.cache.execute.ResultCollector;
import com.vmware.demo.sgf.lucene.LuceneGemFireRepository;
import com.vmware.demo.sgf.lucene.LuceneIndexRepoFactory;
import com.vmware.demo.sgf.lucene.cache.SearchResultCollector;
import com.vmware.demo.sgf.lucene.client.ClientClusterSearch;
import com.vmware.demo.sgf.lucene.client.StreamResultCallback;
import com.vmware.demo.sgf.lucene.domain.SearchType;
import com.vmware.demo.sgf.lucene.domain.SearcheCriteria;
import com.vmware.example.lucene.domain.Instrument;

/**
 * @author Lyndon Adams
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:app-context.xml"})
public class SearchUsingFunctionTest {

	@BeforeClass
	public static void initClasses(){
		new Instrument();
	}
	
	@Test
	public void lookupSingleSymbol() {
		System.out.println("\n------------- lookupSingleSymbol -------------");
		List results = ClientClusterSearch.search("symbol", new String[]{"AACOW"}, "Instrument");
		for(Object r : results ){
			System.out.println( r );
		}
	}

	@Test
	public void lookupMultipleSymbols() {
		System.out.println("\n------------- lookupMultipleSymbols -------------");
		List results = ClientClusterSearch.search( "description", new String[]{"Acquisition"}, "Instrument");
		for(Object r : results ){
			System.out.println( r );
		}
	}
	
	@Test
	public void lookupPartialDescription() {
		System.out.println("\n------------- lookupPartialdescription -------------");
		List results = ClientClusterSearch.search( "description", new String[]{"Acqu*"}, "Instrument");
		for(Object r : results ){
			System.out.println( r );
		}
	}
		
	@Test
	public void streamSearchResults() {
		System.out.println("\n------------- streamSearchResults -------------");
		
		StreamResultCallback callback =new StreamResultCallback(){
			
			int result = 0;
			
			@Override
			public void onEvent(Object o) {
				System.out.println( ++result + " " + o );
			}

			@Override
			public void onComplete() {
				System.out.println("Completed streaming results");
			}
			
		};
		
		ClientClusterSearch.streamSearch( "description", new String[]{"Acqu*"}, "Instrument", callback);
	}
	
	
	@Test public void findAllDocuments(){
		try {
			LuceneGemFireRepository repo = LuceneIndexRepoFactory.getInstance();
			
			List obj = (List) repo.findAll( );
			
			System.out.println("Size: " + obj.size());

			assertFalse( ( obj == null) ? true : false );
			
		} catch(IOException e){
			throw new RuntimeException( e);
		}
		
	}
}
