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
package com.vmware.example.lucene.domain;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.gemstone.gemfire.DataSerializable;
import com.gemstone.gemfire.Instantiator;
import com.vmware.demo.sgf.lucene.annotations.Searchable;

/**
 * @author Lyndon Adams
 *
 */
public class Instrument implements DataSerializable {
	
	static {
		Instantiator.register(new Instantiator(Instrument.class, 121) {
			 public DataSerializable newInstance() {
			  return new Instrument();
			 }
		});
	}

	@Searchable(isUnqiue=true)
	String symbol;
	
	@Searchable
	String description;
	
	public Instrument(){}

	public Instrument(String symbol, String description) {
		super();
		this.symbol = symbol;
		this.description = description;
	}

	public String getSymbol() {
		return symbol;
	}

	public String getDescription() {
		return description;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.DataSerializable#toData(java.io.DataOutput)
	 */
	@Override
	public void toData(DataOutput out) throws IOException {
		out.writeUTF( symbol );
		out.writeUTF( description );
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.DataSerializable#fromData(java.io.DataInput)
	 */
	@Override
	public void fromData(DataInput in) throws IOException,
			ClassNotFoundException {
		symbol = in.readUTF();
		description = in.readUTF();
	}

	@Override
	public String toString() {
		return "Instrument [symbol=" + symbol + ", description=" + description
				+ "]";
	}
}
