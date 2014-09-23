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
import java.util.Date;

import com.gemstone.gemfire.DataSerializable;
import com.gemstone.gemfire.DataSerializer;
import com.gemstone.gemfire.Instantiator;

/**
 * @author Lyndon Adams
 *
 */
public class InstrumentPriceKey extends InstrumentKey implements DataSerializable {
	
	static {
		Instantiator.register(new Instantiator(InstrumentPriceKey.class, 117) {
			 public DataSerializable newInstance() {
			  return new InstrumentPriceKey();
			 }
		});
	}
	
	Date date;
	
	public InstrumentPriceKey(){
		super();
	}

	public InstrumentPriceKey(String exchange, String symbol, Date date) {
		super(exchange, symbol);
		this.date = date;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (date.getDate() >>> 32);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		InstrumentPriceKey other = (InstrumentPriceKey) obj;
		if (date != other.date)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.DataSerializable#toData(java.io.DataOutput)
	 */
	@Override
	public void toData(DataOutput out) throws IOException {
		super.toData(out);
		DataSerializer.writeDate(date, out);
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.DataSerializable#fromData(java.io.DataInput)
	 */
	@Override
	public void fromData(DataInput in) throws IOException,
			ClassNotFoundException {
		super.fromData(in);
		date = DataSerializer.readDate(in);
	}
}
