# Assignment 2
### Instructions
No special instructions needed to run program, just run `nz.ac.wgtn.swen301.assignment2.example.LogRunner`
  
You can run `mvn exec:java` to automatically start `LogRunner` for you.
### JSON library discussion
I picked the Jackson library for the following reasons:
- It is the top-most ranked JSON library on Maven Repository
    - Jackson Databind has 30,473 usages vs Gson (second place)'s 24,047
- It showcased high resilience to attacks in the AdaLogics  Core & Databind Security Audit (https://github.com/FasterXML/jackson/blob/master/docs/AdaLogics-Security-Audit-Jackson-2022.pdf)
- It was the fastest library in a number of benchmarks:
  - https://dzone.com/articles/the-ultimate-json-library-jsonsimple-vs-gson-vs-ja
- It has a simple API (excerpt from my submission):
```java
ObjectMapper om = new ObjectMapper();
StringWriter sw = new StringWriter();
HashMap<String, String> payload = new HashMap<>();
payload.put("name", loggingEvent.getLoggerName());
om.writeValue(sw, payload);
```
- It is literally named after me

### Comparisons
**Jackson vs Gson**

| Jackson                                                            | Gson                                                             |
|--------------------------------------------------------------------|------------------------------------------------------------------|
| Named after me                                                     | Not named after me                                               |                                              
| Largest library by Maven downloads                                 | Backed by Google, one of the largest tech companies in the world |
| Independent security audit passed, while Gson has not been audited |                                                                  |
| Backed by consultant firm FasterXML                                |                                                                  |
| Widely available documentation and tutorials for use               | Also a lot of resources available                                |


**Jackson vs JSONP**

| Jackson                                                            | JSONP                                                                  |
|--------------------------------------------------------------------|------------------------------------------------------------------------|
| Named after me                                                     | Not named after me                                                     |                                              
| Largest library by Maven downloads                                 | **Backed by Oracle, the company that maintains Java**                  |
| Independent security audit passed, while Gson has not been audited |                                                                        |
| Backed by consultant firm FasterXML                                |                                                                        |
| Widely available documentation and tutorials for use               | Newer, so there is less documentation, examples, and plugins available |

