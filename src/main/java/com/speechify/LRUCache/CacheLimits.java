package com.speechify.LRUCache;

public class CacheLimits {
    /**
     * @property maxItemsCount
     * Maximum count of items (*inclusive*) that this cache is allowed to contain.
     */
    private final int maxItemsCount;

    public CacheLimits(int maxItemsCount) {
        this.maxItemsCount = maxItemsCount;
    }

    public int getMaxItemsCount() {
        return maxItemsCount;
    }
}