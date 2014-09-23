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
package com.vmware.example.lucene.loader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;

import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Region;
import com.vmware.demo.sgf.lucene.client.ClientBatchOperations;
import com.vmware.example.lucene.domain.Instrument;
import com.vmware.example.lucene.domain.InstrumentKey;

/**
 * @author Lyndon Adams
 *
 */
public class InstrumentWriter implements ItemWriter<Instrument> {
		
	
	// ?
	String exchange = "NASDAQ";
	
	@Qualifier("TargetRegion")
	String targetRegion = "/Instrument";
	
	public InstrumentWriter(){
		super();
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.batch.item.ItemWriter#write(java.util.List)
	 */
	@Async
	@Override
	public void write(List<? extends Instrument> items) throws Exception {
		try {
			Map<InstrumentKey, Instrument> map = new HashMap<InstrumentKey, Instrument>();
			
			for(Instrument i : items) map.put( new InstrumentKey(exchange, i.getSymbol()), i);
			
			ClientBatchOperations.batchLoad(map, targetRegion);
			
		}catch(Exception e){
			throw e;
		}
	}
}
