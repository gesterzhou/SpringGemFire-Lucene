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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Operation;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.execute.FunctionService;
import com.vmware.demo.sgf.lucene.cache.LuceneBatchOperator;
import com.vmware.demo.sgf.lucene.domain.batch.BatchKeyValue;
import com.vmware.demo.sgf.lucene.domain.batch.BatchOperation;

/**
 * @author Lyndon Adams
 *
 */
public class ClientBatchOperations {
	
	private ClientBatchOperations(){}

	/**
	 * Load a set of data in to GemFire using a batch process.
	 * 
	 * @param map - Key/Value pairs to load.
	 * @param regionName - Target region path.
	 * @return	- True is operation was successful false otherwise.
	 */
	public static boolean batchLoad(final Map<? extends Object,? extends Object> map, final String regionName){
		return executeBatchOperation(map, regionName, Operation.CREATE);
	}

	/**
	 * Update a set of data in to GemFire using a batch process.
	 * 
	 * @param map - Key/Value pairs to update.
	 * @param regionName - Target region path.
	 * @return	- True is operation was successful false otherwise.
	 */
	public static boolean batchUpdate(final Map<? extends Object,? extends Object> map, final String regionName){
		return executeBatchOperation(map, regionName, Operation.UPDATE);
	}
	
	/**
	 * Delete a set of data in to GemFire using a batch process.
	 * 
	 * @param map - Key/Value pairs to delete.
	 * @param regionName - Target region path.
	 * @return	- True is operation was successful false otherwise.
	 */
	public static boolean batchDelete(final Map<? extends Object,? extends Object> map, final String regionName){
		return executeBatchOperation(map, regionName, Operation.REMOVE);
	}
	
	/**
	 * 
	 * @param map
	 * @param regionName
	 * @param operation
	 * @return
	 */
	private static boolean executeBatchOperation(final Map<? extends Object,? extends Object> map, final String regionName, Operation operation){
		boolean isSuccess = true;
		
		// Get the correct pool of servers for the region to operate upon.
		ClientCache cache = (ClientCache) CacheFactory.getAnyInstance();
		Region region =  cache.getRegion( regionName );
		
		Set<BatchKeyValue> filterSet = new HashSet<>();

		for(Entry<? extends Object, ? extends Object> e : map.entrySet()){
			filterSet.add( new BatchKeyValue(e.getKey(), e.getValue()) );
		}
		
		FunctionService.onRegion( region ).withArgs( new BatchOperation( operation )).withFilter(filterSet ).execute( LuceneBatchOperator.ID );
		return isSuccess;
	}

}
