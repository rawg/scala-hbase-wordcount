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

import java.util.StringTokenizer
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase._
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.io._
import org.apache.hadoop.hbase.mapreduce._
import org.apache.hadoop.io._
import org.apache.hadoop.mapreduce._
import scala.collection.JavaConversions._



class WordCountMapper extends TableMapper[Text, LongWritable] {
  type Contxt = Mapper[ImmutableBytesWritable, Result, Text, LongWritable]#Context

  val word = new Text
  val one = new LongWritable(1)

  override def map(key: ImmutableBytesWritable, value: Result, context: Contxt) {

    val cell = value.getColumnLatestCell(Families.content, Qualifiers.text)
    val text = new String(CellUtil.cloneValue(cell)).toLowerCase

    val tokenizer = new StringTokenizer(text)
    while (tokenizer.hasMoreTokens) {  // converting to scala Enumerable gives us foreach(Object => Unit), not foreach(String => Unit)
      val term = tokenizer.nextToken
      if (term.matches("[a-zA-Z0-9]+")) {
        word.set(term)
        context.write(word, one)
      }
    }

  }

}

