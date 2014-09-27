GiftGiving
===========

assumption: here I only used all products under boots category to illustrate my code. 

This code generates all possible combinations that fits within +/-($RANGE)/2 of the total sum
As several of the products has the same prices there are many many possible outputs 

Some ideas to speed up the code:

 >> In my code I used a loop to retrieve all 125 pages of products sequentially. However, this fetching is embarrassingly parallel. This process can be executed in parallel very easily by spawning 100 threads and assigning each thread to retrieve 1/2 pages. With proper synchronization this process will run significantly faster than the sequential retrieval.
 
 >> Once all the products are retrieved, the makeList method in the code starts generating possible combinations of products close to the users budget. In order to improve the response time to the user, we can easily spawn another thread that will take say the 100 possible combinations and show it to the user. Then if the user wants to see more suggestions this thread can again pull up next 100 solutions and so on. But this process can eventually create a producer/consumer problem. Which we can solve using proper synchronization (using semaphors).   
