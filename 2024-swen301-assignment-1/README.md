## SWEN301 Assignment 1 Template

### 
Provide a light-weight documentation by editing README.md, with a discussion (less than 100 words) whether your design is prone to memory leaks by interfering with Garbage Collection and how this has been or could be addressed. [1 marks]


### Discussion

```
Provide a light-weight documentation by editing README.md, with
a discussion (less than 100 words) whether your design is prone to
memory leaks by interfering with Garbage Collection and how this
has been or could be addressed. [1 marks]
```

My design is not prone to memory leaks because I use Google's Guava `CacheBuilder` and `Cache` classes that provide concurrency and memory-safety.
Java's in-built memory safety guarantees ensure that cache objects cannot leak. The cache automatically removes objects after 10 minutes, and objects are
prevented from being stale because all of my methods automatically call `invalidate` whenever an object is (a) updated, or (b) deleted. This means at the next call to
`fetchStudent`, the new object is fetched from the database. 
Guava's `Cache` is also able to respond to memory pressure from Java's garbage collector, so it can automatically start invalidating cache entries to free up memory.

To set up the project run the following command first from a terminal:

`mvn install:install-file -Dfile=lib/studentdb-2.0.0.jar -DgroupId=nz.ac.wgtn.swen301 -DartifactId=studentdb -Dversion=2.0.0 -Dpackaging=jar`