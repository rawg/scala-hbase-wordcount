/**
  * An example MapReduce application using Scala and HBase.
  *
  *
  * The MIT License (MIT)
  *
  * Copyright (c) 2014 Jeremy Fisher
  *
  * Permission is hereby granted, free of charge, to any person obtaining a copy
  * of this software and associated documentation files (the "Software"), to deal
  * in the Software without restriction, including without limitation the rights
  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  * copies of the Software, and to permit persons to whom the Software is
  * furnished to do so, subject to the following conditions:
  *
  * The above copyright notice and this permission notice shall be included in all
  * copies or substantial portions of the Software.
  *
  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  * SOFTWARE.
  *
  * @author Jeremy Fisher <jeremy@rentawebgeek.com>
  */


package com.rentawebgeek.hbmr


import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase._
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.io._
import org.apache.hadoop.hbase.mapreduce._
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.io._
import org.apache.hadoop.mapreduce._
import scala.collection.JavaConversions._


class WordCountReducer extends TableReducer[Text, LongWritable, ImmutableBytesWritable] {
  type Contxt = Reducer[Text, LongWritable, ImmutableBytesWritable, Mutation]#Context

  override protected def reduce(
    key: Text,
    values: java.lang.Iterable[LongWritable],  // be sure to get the right type
    context: Contxt
  ) {

    val count = values.foldLeft(0L) { (tally, i) => tally + i.get }

    val put = new Put(key.toString.getBytes, count)  // be sure to comment on toString.getBytes
    put.add(Families.content, Qualifiers.count, Bytes.toBytes(count))

    context.write(null, put)

  }

}


