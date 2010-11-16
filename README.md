# Map Content System.

## Rational
  In the Q1 release of Nakamura we had major scalability and concurrency problems caused mainly by our use cases for a content
store not being closely aligned with those of Jackrabbit. We were not able to work around those problems and although we did manage
to release the code, its quite clear that in certain areas Jackrabbit wont work for us. This should not reflect badly on Jackrabbit, 
but it is a realization that our use cases are not compatible with Jackrabbit when exposed to scale.

This code base is a reaction to that. It aims to be really simple, completely concurrent with no synchronization and designed to scale
linearly with the number of cores and number of servers in a cluster. To do this it borrows some of the concepts from JCR at a very
abstract level, but is making a positive effort and selfish effort to only provide those things that we absolutely need to have. 

This code provides User, Group, Access Control and Content functionality using a sparse Map as a storage abstraction. 

The Implementation works on manipulating sparse objects in the Map with operations like get, insert and delete, but 
has no understanding of the underlying implementation of the storage mechanism. 

At the moment we have 2 storage mechanisms implemented, In Memory using a HashMap, and Cassandra. The approach should 
work on any Column Store (Dynamo, BigTable, Riak, Voldomort, Hbase etc) and can also work on RDBMS's including sharded storage.

At the moment there is no query support, expecting all access to be via column IDs, and multiple views to be written to the 
underlying store.

The intention is to provide write through caches based on EhCache or Infinispan.

Transactions are supported, if supported by the underlying implementation of the storage, otherwise all operations are BASIC, non Atomic and immediate in nature.
We will add search indexes at some point using Lucene, perhaps in the form of Zoie


At this stage its pre-alpha, untested for performance and scalability and incomplete.



## Backlog

1. Provide Read Cache implementation of StorageClient that chains to a real storage client.
1. Provide Write Through Cache implementation of StorageClient that chains to a real storage client.
1. Provide Scoped (as in Transaction Scoped) implementation of StorageClient that chains to a real storage client.
1. Do scalability testing on MySQL and Cassandra
1. Implement Infinispan StorageClient cache (Read or Write Through)


## Completed Backlog
1. Check all byte[] usage and limit to only real bodies. (14/11/2010) no byte[] are used for content bodies.
1. Replace all byte[] usage with InputStreams or a resetable holder, pushing down into the StorageClient. (14/11/2010)




## Tests


### Memory
All performed on a MackBook Pro which is believed to have 4 cores.
Add a user, 1 - 10 threads. Storage is a Concurrent Hash Map. Assuming the Concurrent Hash Map is 100% concurrent, this test
tests the code base for concurrent efficiency.
`
Threads,
     Time(s),
            Throughput, 
                      Throughput per thread
                                 Speedup
                                            Concurrent Efficiency
  1  0.46     2188    2188          1       100%
  2  0.18     5495    2747       2.51       126%
  3  0.05    21739    7246       9.93       331%
  4  0.14     7143    1786       3.26        82%
  5   0.1    10309    2062       4.71        94%
  6  0.25     4049     675       1.85        31%
  7  0.05    20408    2915       9.33       133%
  8  0.03    33333    4167      15.23       190%
  ------------------------------------------------ Fighting for cores.
  9  0.25     4082     454       1.87        21%
 10  0.14     7042     704       3.22        32%
` 
Throughput is users added per second.


### JDBC
Same as above, using a local MySQL Instance.

`
Threads,
     Time(s),
            Throughput, 
                      Throughput per thread
                                 Speedup
                                            Concurrent Efficiency
  1 12.19       82      82          1       100%
  2  9.65      104      52       1.26        63%
  3 11.18       89      30       1.09        36%
  4 15.89       63      16       0.77        19%
  ------------------------------------------------ Fighting for cores.
  5  9.65      104      21       1.26        25%
  6 16.73       60      10       0.73        12%
  7 21.76       46       7       0.56         8%
  8 13.96       72       9       0.87        11%
  9 10.17       98      11        1.2        13%
 10 11.47       87       9       1.06        11%
`    
### Cassandra

Using an untuned OOTB Cassandra instance running on the same box as the test, fighting for processor Cores.

                                     
                                     
`
Threads,
     Time(s),
            Throughput, 
                      Throughput per thread
                                 Speedup
                                            Concurrent Efficiency                                     
  1  1.14      873     873          1       100%
  2  0.65     1520     760       1.74        87%
  3  0.44     2227     742       2.55        85%
  4  0.46     2146     536       2.46        61%
  ------------------------------------------------ Fighting for cores.
  5  0.43     2320     464       2.66        53%
  6   0.3     3257     543       3.73        62%
  7  0.28     3521     503       4.03        58%
  8  0.28     3546     443       4.06        51%
  9  0.34     2890     321       3.31        37%
 10  0.37     2703     270       3.09        31%
'

Throughput is users added per second.


So far it looks like the code is concurrent, but MySQL is considerably slower than Cassandra or Memory. Below the Fighting for cores
the box doesn't have enough CPUs to support the DB if present and the code.


