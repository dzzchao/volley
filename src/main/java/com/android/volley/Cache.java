/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.volley;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/** An interface for a cache keyed by a String with a byte array as data. */
public interface Cache {
    /**
     * Retrieves an entry from the cache.
     * 通过key获取请求的缓存实体
     *
     * @param key Cache key
     * @return An {@link Entry} or null in the event of a cache miss
     */
    Entry get(String key);

    /**
     * Adds or replaces an entry to the cache.
     *  存入一个请求的缓存实体
     * @param key Cache key
     * @param entry Data to store and metadata for cache coherency, TTL, etc.
     */
    void put(String key, Entry entry);

    /**
     * Performs any potentially long-running actions needed to initialize the cache; will be called
     * from a worker thread.
     */
    void initialize();

    /**
     * Invalidates an entry in the cache.
     * 使缓存中的数据无效
     *
     * @param key Cache key
     * @param fullExpire True to fully expire the entry, false to soft expire
     */
    void invalidate(String key, boolean fullExpire);

    /**
     * Removes an entry from the cache. 删除指定的缓存实体
     *
     * @param key Cache key
     */
    void remove(String key);

    /** Empties the cache. */
    void clear();

    /** Data and metadata for an entry returned by the cache. */
    class Entry {

        /** The data returned from cache. 缓存数据 */
        public byte[] data;

        /** ETag for cache coherency. HTTP响应首部中用于缓存新鲜度的Etag*/
        public String etag;

        /** Date of this response as reported by the server. HTTP 响应首部中的响应产生时间*/
        public long serverDate;

        /** The last modified date for the requested object. 请求对象的最后修改日期 */
        public long lastModified;

        /** TTL for this record. 缓存的过期时间*/
        public long ttl;

        /** Soft TTL for this record. 缓存的新鲜时间*/
        public long softTtl;

        /**
         * Response headers as received from server; must be non-null. Should not be mutated
         * directly.
         *
         * <p>Note that if the server returns two headers with the same (case-insensitive) name,
         * this map will only contain the one of them. {@link #allResponseHeaders} may contain all
         * headers if the {@link Cache} implementation supports it.
         *
         * 响应的 Headers
         */
        public Map<String, String> responseHeaders = Collections.emptyMap();

        /**
         * All response headers. May be null depending on the {@link Cache} implementation. Should
         * not be mutated directly.
         *
         *
         */
        public List<Header> allResponseHeaders;

        /** True if the entry is expired.  判断缓存是否过期，过期缓存不能继续使用 */
        public boolean isExpired() {
            return this.ttl < System.currentTimeMillis();
        }

        /** True if a refresh is needed from the original data source. 判断缓存是否新鲜，不新鲜的缓存需要发到服务端做新鲜度的检测*/
        public boolean refreshNeeded() {
            return this.softTtl < System.currentTimeMillis();
        }
    }
}
