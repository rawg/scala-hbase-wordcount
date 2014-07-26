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
import org.apache.hadoop.io._
import org.apache.hadoop.mapreduce._


object Driver {

  def main(args: Array[String]) {
    val conf = HBaseConfiguration.create()

    /* Create or truncate our sink */
    createSchema(conf)

    val job = Job.getInstance(conf, "WordCount")

    /* This necessitates a class be defined. Scala technically generates a Driver$ class for the
     * companion object, but `classOf[Driver$]` makes the compiler unhappy
     */
    job.setJarByClass(classOf[Driver])


    /* An instance of Scan is used by TableMapReduceUtil to filter columns and
     * tune query parameters. There's a lot going on in the hbase.mapreduce that makes this
     * work – don't expect to quickly duplicate it, just trust that it works.
     */
    val scan = new Scan()
    scan.setCaching(500)         // 1 is the default in Scan, which will be bad for MapReduce jobs
    scan.setCacheBlocks(false)   // don't set to true for MR jobs


    /* The signature for addColumn requires byte arrays. We provide HStrings that are automatically
     * converted to byte arrays in order to let the compiler catch any spelling mistakes we might
     * make.
     */
    scan.addColumn(Families.content, Qualifiers.text)


    TableMapReduceUtil.initTableMapperJob(
      Tables.webTable.bytes,     // input HBase table name
      scan,                      // Scan instance to control CF and attribute selection
      classOf[WordCountMapper],  // mapper class
      classOf[Text],             // mapper output key class
      classOf[LongWritable],     // mapper output value class
      job
    )


    TableMapReduceUtil.initTableReducerJob(
      Tables.wordCount,          // Table name
      classOf[WordCountReducer], // Reducer class
      job
    )

    val success = (job.waitForCompletion(true))
    System.exit(if (success) 1 else 0)

  }


  def createSchema(conf: Configuration) {

    val admin = new HBaseAdmin(conf)

    try {
      admin.disableTable(Tables.wordCount.name)
      admin.deleteTable(Tables.wordCount.name)
    } catch {
      case e: TableNotFoundException => // Running for the first time
    }

    val desc = new HTableDescriptor(Tables.wordCount.bytes) // Be explicit about providing bytes
    desc.addFamily(new HColumnDescriptor(Families.content.bytes))
    admin.createTable(desc)

  }


}


// Just makes Job.setJarByClass work
class Driver { }

