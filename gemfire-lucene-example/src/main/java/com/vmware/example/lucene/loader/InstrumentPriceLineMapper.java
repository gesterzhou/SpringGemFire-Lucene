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

import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

import org.springframework.batch.item.file.LineMapper;

import com.vmware.example.lucene.domain.InstrumentPrice;

/**
 * @author Lyndon Adams
 *
 */
public class InstrumentPriceLineMapper implements LineMapper<InstrumentPrice>{

	
	static SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    
	/* (non-Javadoc)
	 * @see org.springframework.batch.item.file.LineMapper#mapLine(java.lang.String, int)
	 */
	@Override
	public InstrumentPrice mapLine(String line, int lineNumber)
			throws Exception {
		StringTokenizer lineTokenizer = new StringTokenizer(line, ",");
		InstrumentPrice price = new InstrumentPrice();
		
		price.setSymbol( lineTokenizer.nextToken() );
		
		price.setDate( formatter.parse( lineTokenizer.nextToken() ) );
		price.setHigh( Double.parseDouble( lineTokenizer.nextToken() ));
		price.setLow( Double.parseDouble( lineTokenizer.nextToken() ));
		price.setOpen( Double.parseDouble( lineTokenizer.nextToken() ));
		price.setClose( Double.parseDouble( lineTokenizer.nextToken() ));
		price.setVolume( Long.parseLong( lineTokenizer.nextToken() ));
		
		
		return price;
	}
}
