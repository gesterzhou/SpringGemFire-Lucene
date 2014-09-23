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

import java.util.HashMap;

/**
 * Service that caches index region pointers to provide region to index mapping.
 * 
 * @author Lyndon Adams
 *
 */
public final class LuceneIndexService {

	static HashMap<String, LuceneGemFireRepository> map = new HashMap<String, LuceneGemFireRepository>();
	
	public static void registerIndex(final String region, LuceneGemFireRepository repo){
		if( !map.containsKey( region )) map.put(region, repo);
	}
	
	public static void unregisterIndex(final String region){
		if( map.containsKey( region )) map.remove( region );
	}
	
	public static LuceneGemFireRepository getRegionIndex(final String region){
		return map.get( region );
	}
}