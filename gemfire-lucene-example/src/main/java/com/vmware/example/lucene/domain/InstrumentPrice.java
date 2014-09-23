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
import com.vmware.demo.sgf.lucene.annotations.Searchable;

/**
 * @author Lyndon Adams
 *
 */
public class InstrumentPrice implements DataSerializable {
	
	static {
		Instantiator.register(new Instantiator(InstrumentPrice.class,118) {
			 public DataSerializable newInstance() {
			  return new InstrumentPrice();
			 }
		});
	}

	@Searchable
	String symbol;
	
	Date date;
	
	double high, low, open, close;
	long volume;
	
	public InstrumentPrice() {}

	public InstrumentPrice(String symbol, Date date, double high, double low, double open, double close, long volume) {
		super();
		this.symbol = symbol;
		this.date = date;
		this.high = high;
		this.low = low;
		this.open = open;
		this.close = close;
		this.volume = volume;
	}
	
	public String getSymbol(){
		return symbol;
	}
	
	public Date getDate(){
		return date;
	}

	public double getHigh() {
		return high;
	}

	public double getLow() {
		return low;
	}

	public double getClose() {
		return close;
	}

	public long getVolume() {
		return volume;
	}
	
	public double getOpen() {
		return open;
	}

	public void setOpen(double open) {
		this.open = open;
	}

	public void setSymbol(final String symbol){
		this.symbol = symbol.intern();
	}
	
	public void setDate(final Date date){
		this.date = date;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	public void setLow(double low) {
		this.low = low;
	}

	public void setClose(double close) {
		this.close = close;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.DataSerializable#toData(java.io.DataOutput)
	 */
	@Override
	public void toData(DataOutput out) throws IOException {
		out.writeUTF( symbol );
		DataSerializer.writeDate(date, out);
		out.writeDouble( high);
		out.writeDouble( low );
		out.writeDouble( open );
		out.writeDouble( close);
		out.writeLong( volume );
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.DataSerializable#fromData(java.io.DataInput)
	 */
	@Override
	public void fromData(DataInput in) throws IOException,
			ClassNotFoundException {
		symbol = in.readUTF();
		date = DataSerializer.readDate(in);
		high = in.readDouble();
		low  = in.readDouble();
		open = in.readDouble();
		close = in.readDouble();
		volume = in.readLong();
	}

	@Override
	public String toString() {
		return "InstrumentPrice [symbol=" + symbol + ", date=" + date
				+ ", high=" + high + ", low=" + low + ", open=" + open
				+ ", close=" + close + ", volume=" + volume + "]";
	}
}

