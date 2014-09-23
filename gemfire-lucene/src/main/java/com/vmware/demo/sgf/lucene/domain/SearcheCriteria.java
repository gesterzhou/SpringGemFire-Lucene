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
package com.vmware.demo.sgf.lucene.domain;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gemstone.gemfire.DataSerializable;
import com.gemstone.gemfire.DataSerializer;
import com.gemstone.gemfire.Instantiator;

/**
 * Search criteria for Lucene index platform.
 * 
 * @author Lyndon Adams
 *
 */
public final class SearcheCriteria extends LuceneKey  {

	static {
		Instantiator.register(new Instantiator(SearcheCriteria.class, 80001) {
			 public DataSerializable newInstance() {
			  return new SearcheCriteria();
			 }
		});
	}
	
	int maxItems = Integer.MAX_VALUE;
	//List<String> searchItems;
	String[] searchItems;
	SearchType type;
	
	String regionName;
	
	public SearcheCriteria(){}
	
	/**
	 * @param searchField
	 * @param searchText
	 */
	public SearcheCriteria(String searchField, String searchText, SearchType type, String regionName) {
		super(searchField, searchText);
		this.type = type;
		this.regionName = regionName;
	}

	
	public SearcheCriteria(String searchField, String[] searchItems, SearchType type,String regionName) {
		super();
		super.searchField = searchField;
		this.searchItems = searchItems;
		this.type = type;
		this.regionName = regionName;
	}

	public SearcheCriteria(String searchField, String[] searchItems, SearchType type, String regionName, int maxItems) {
		super();
		super.searchField = searchField;
		this.searchItems = searchItems;
		this.type = type;
		this.regionName = regionName;
		this.maxItems = maxItems;
	}
	
	public SearchType getSearchType(){
		return type;
	}
	
	public List<String> getSearchItems(){
		return Arrays.asList( searchItems );
	}
	
	public int getMaxItems(){
		return maxItems;
	}

	public String getRegionName(){
		return regionName;
	}
	
	/**
	 * Is the search criteria a multiple search operation.
	 * 
	 * @return True if multiple text items to search for otherwise false.
	 */
	public boolean isMultipleTextSearch(){
		return (searchItems!=null) ? true : false;
	}


	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.DataSerializable#toData(java.io.DataOutput)
	 */
	@Override
	public void toData(DataOutput out) throws IOException {
		super.toData(out);
		DataSerializer.writePrimitiveInt( maxItems, out);
		
		DataSerializer.writeObjectArray( searchItems, out);
		
		//DataSerializer.writeArrayList( ((ArrayList<String>)searchItems), out);
		DataSerializer.writeEnum( type, out);
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.DataSerializable#fromData(java.io.DataInput)
	 */
	@Override
	public void fromData(DataInput in) throws IOException,
			ClassNotFoundException {
		super.fromData(in);
		maxItems = DataSerializer.readPrimitiveInt(in);
		
		searchItems = (String[]) DataSerializer.readObjectArray(in);
		//searchItems =  Arrays.asList( arr );
		 
		//searchItems = DataSerializer.readArrayList(in);
		type = DataSerializer.readEnum(SearchType.class, in);
	}
}
