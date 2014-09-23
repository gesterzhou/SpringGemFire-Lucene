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
package com.vmware.demo.sgf.lucene;

import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.execute.FunctionService;
import com.gopivotal.gemfire.utils.spring.CacheSpringProcess;
import com.vmware.demo.sgf.lucene.cache.LuceneBatchOperator;
import com.vmware.demo.sgf.lucene.cache.LuceneSearchFunction;
import com.vmware.demo.sgf.lucene.cache.SearchCountItems;

/**
 * Lucene enabled cache server.
 * 
 * @author Lyndon Adams
 *
 */
public class LuceneEnabledServer extends CacheSpringProcess {
	
	/**
	 * Register server functions to perform operations on the index.
	 */
	private void registerFunctions(){		
		if( !FunctionService.isRegistered( LuceneSearchFunction.ID )) 
			FunctionService.registerFunction( new LuceneSearchFunction( ) );
		if( !FunctionService.isRegistered( SearchCountItems.ID )) 
			FunctionService.registerFunction( new SearchCountItems( ) );
		if( !FunctionService.isRegistered( LuceneBatchOperator.ID )) 
			FunctionService.registerFunction( new LuceneBatchOperator( ) );
	}
	
	public static void main(String... args) throws InterruptedException{
		LuceneEnabledServer server = new LuceneEnabledServer();
		server.initialize();
		server.registerFunctions();
		server.runForever();
	}
}
