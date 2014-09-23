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
package com.vmware.demo.sgf.lucene.domain.batch;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


import com.gemstone.gemfire.DataSerializable;
import com.gemstone.gemfire.DataSerializer;
import com.gemstone.gemfire.Instantiator;
import com.gemstone.gemfire.cache.Operation;

/**
 * @author Lyndon Adams
 *
 */
public class BatchOperation implements DataSerializable  {
	
	static {
		Instantiator.register(new Instantiator(BatchOperation.class, 80003) {
			 public DataSerializable newInstance() {
			  return new BatchOperation();
			 }
		});
	}
	
	Operation operation;
	
	public BatchOperation(){}
	
	public BatchOperation(Operation op){
		operation = op;
	}

	public Operation getOperation() {
		return operation;
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.DataSerializable#fromData(java.io.DataInput)
	 */
	@Override
	public void fromData(DataInput in) throws IOException,
			ClassNotFoundException {
		operation = DataSerializer.readObject(in);
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.DataSerializable#toData(java.io.DataOutput)
	 */
	@Override
	public void toData(DataOutput out) throws IOException {
		DataSerializer.writeObject(operation, out);
	}
}
