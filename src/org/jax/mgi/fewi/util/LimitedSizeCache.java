package org.jax.mgi.fewi.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* Is: a size-limited cache of (type T) elements
 * Has: a cache of elements, accessed by a String key
 * Does:
 *   1. stores elements associated with keys (like a Map)
 *   2. automatically times-out and removes the oldest key/element pair when its spot is needed for a newer one
 */
public class LimitedSizeCache<T> {
	public int cacheSize = 30;			// default number of entries to cache; can be changed
	public List<String> keys;			// list of keys, ordered from oldest to newest
	public Map<String,T> cache;			// Map from each key to its corresponding element
	
	// constructor - keep default cache size
	public LimitedSizeCache() {
		this.keys = new ArrayList<String>(cacheSize);
		this.cache = new HashMap<String,T>();
	}
	
	// constructor - pick a different cache size
	public LimitedSizeCache(int cacheSize) {
		this.cacheSize = cacheSize;
		this.keys = new ArrayList<String>(cacheSize);
		this.cache = new HashMap<String,T>();
	}
	
	// current number of items in the cache
	public int size() {
		return this.cache.size();
	}

	// empty the cache
	public void clear() {
		this.keys.clear();
		this.cache.clear();
	}
	
	// return true if the given key is in the cache, false if not
	public boolean containsKey(String key) {
		return this.cache.containsKey(key);
	}
	
	// If key is in the cache, return its associated element.  Otherwise return null.
	public T get(String key) {
		if (this.cache.containsKey(key)) {
			return this.cache.get(key);
		}
		return null;
	}
	
	// Update the cache so that the given key is associated with the given element.  Also
	// removes the least recently accessed key, if needed to make room for this one.
	public void put(String key, T element) {
		this.keys.add(key);	
		this.cache.put(key, element);

		synchronized(this) {
			// If we've crossed our maximum cache size, remove the oldest key/element pairs.
			while (this.keys.size() > this.cacheSize) {
				String toDelete = this.keys.get(0);
				this.keys.remove(0);
				this.cache.remove(toDelete);
			}
		}
	}
}
