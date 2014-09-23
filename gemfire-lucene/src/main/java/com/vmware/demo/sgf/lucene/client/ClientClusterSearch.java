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
package com.vmware.demo.sgf.lucene.client;

import java.util.List;

import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.Pool;
import com.gemstone.gemfire.cache.client.PoolManager;
import com.gemstone.gemfire.cache.execute.FunctionService;
import com.gemstone.gemfire.cache.execute.ResultCollector;
import com.vmware.demo.sgf.lucene.cache.LuceneSearchFunction;
import com.vmware.demo.sgf.lucene.cache.SearchCountItems;
import com.vmware.demo.sgf.lucene.cache.SearchResultCollector;
import com.vmware.demo.sgf.lucene.cache.SearchResultCounter;
import com.vmware.demo.sgf.lucene.domain.SearchType;
import com.vmware.demo.sgf.lucene.domain.SearcheCriteria;

/**
 * @author Lyndon Adams
 *
 */
public final class ClientClusterSearch {

	private ClientClusterSearch(){}
	
	/**
	 * Perform a cluster search for the given field and searchText items on a region.
	 * 
	 * @param field 	 - Field to perform search upon.
	 * @param searchText - Array list of search text items.
	 * @param regionName - Actual region path.
	 * @param maxItems 	 - Maximum number of items to return.
	 * @return			 - List of found elements.
	 */
	public static List search(String field, String[] searchText, String regionName){
		return search(field, searchText, regionName, Integer.MAX_VALUE);
	}
	
	/**
	 * Perform a cluster search for the given field and searchText items on a region.
	 * 
	 * @param field 	 - Field to perform search upon.
	 * @param searchText - Array list of search text items.
	 * @param regionName - Actual region path.
	 * @param maxItems 	 - Maximum number of items to return.
	 * @return			 - List of found elements.
	 */
	public static List search(String field, String[] searchText, String regionName, int maxItems){
		
		// Get the correct pool of servers for the region to operate upon.
		ClientCache cache = (ClientCache) CacheFactory.getAnyInstance();
		Region region =  CacheFactory.getAnyInstance().getRegion( regionName );
		Pool pool = PoolManager.find( region );		

		SearcheCriteria sc = new SearcheCriteria(field, searchText, SearchType.UNIQUE, regionName, maxItems);
		
		ResultCollector rc = FunctionService.onRegion( region ).withArgs( sc ).withCollector( new SearchResultCollector( )).execute( LuceneSearchFunction.ID);
		return (List) rc.getResult();
	}
	

	/**
	 * Stream search results as they are found.
	 * 
	 * @param field 	 - Field to perform search upon.
	 * @param searchText - Array list of search text items.
	 * @param regionName - Actual region path.
	 */
	public static void streamSearch(String field, String[] searchText, String regionName, final StreamResultCallback callback ){
		
		// Get the correct pool of servers for the region to operate upon.
		ClientCache cache = (ClientCache) CacheFactory.getAnyInstance();
		Region region =  CacheFactory.getAnyInstance().getRegion( regionName );
		Pool pool = PoolManager.find( region );		

		SearcheCriteria sc = new SearcheCriteria(field, searchText , SearchType.UNIQUE, regionName );
		
		// Stream results to callback argument and block until final received. 
		ResultCollector rc = FunctionService.onRegion( region ).withArgs( sc ).withCollector( new SearchResultCollector( callback )).execute( LuceneSearchFunction.ID);
		List results = (List) rc.getResult();
	}

	/**
	 * Get a count of the number of found elements.
	 * 
	 * @param field 	 - Field to perform search upon.
	 * @param searchText - Array list of search text items.
	 * @param regionName - Actual region path.
	 * @return			 - Count of found elements.
	 */
	public static long searchCount(String field, String[] searchText, String regionName){
		
		// Get the correct pool of servers for the region to operate upon.
		ClientCache cache = (ClientCache) CacheFactory.getAnyInstance();
		Region region =  CacheFactory.getAnyInstance().getRegion( regionName );

		SearcheCriteria sc = new SearcheCriteria(field, searchText, SearchType.UNIQUE, regionName);
		
		ResultCollector rc = FunctionService.onRegion( region ).withArgs( sc ).withCollector( new SearchResultCounter( )).execute( SearchCountItems.ID);
		return  (Long) rc.getResult();
	}
	

}
