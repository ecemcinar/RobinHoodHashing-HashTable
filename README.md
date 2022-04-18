# RobinHoodHashing-HashTable

![robin-hood-hashing](https://user-images.githubusercontent.com/81980502/163891322-3669e41b-0d94-4fde-b6c5-1f6a250e99d3.jpg)

### Steps
- Calculate the hash value and initial index of the entry to be inserted
- Search the position linearly
- While searching, the distance from initial index is kept which is called DIB (Distance from Initial Bucket)
- If empty bucket is found, we can insert the entry with DIB here
- If we encounter a entry which has less DIB than the one of the entry to be inserted, swap them.
