# url-cache

Read a file from the Internet, unless we've already fetched it,
in which case, read it from the local cache.

This is minimalist. Known issues:

- Doesn't do connection pooling
- Multiple requests for the same file from different threads may race
  and result in a corrupt file
- The naming schema for `fetchNamed` can result in conflicts

...in other words, it's not bullet proof. But it's probably suitable
for most purposes.

```scala
import com.cldellow.urlcache.Cache

// Caches to /tmp/cache/d6/57/d657127c40d4ecfe29c74c366b3198bd
Cache.fetch("https://google.com/robots.txt")

// Caches to /tmp/mydir/d6/57/d657127c40d4ecfe29c74c366b3198bd
new Cache("/tmp/mydir").fetch("https://google.com/robots.txt")

// Cache in /tmp/cache/mydir/d6/57/d657127c40d4ecfe29c74c366b3198bd
Cache.fetch("https://google.com/robots.txt", prefix = "mydir"))

// Cache to /tmp/cache/misc/https_google.com_robots.txt
Cache.fetchNamed("https://google.com/robots.txt")

// Cache first 100 bytes to /tmp/cache/d6/57/d657127c40d4ecfe29c74c366b3198bd-0-100
Cache.fetch("https://google.com/robots.txt", range = (0, 100))
```
