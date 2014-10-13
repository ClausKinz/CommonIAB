/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/

/**
 * This represents map functionality for javascript
 * @param {Object|undefined} linkItems he key-value-pairs is optional
 * if no argument is provided, linkItems === undefined, i.e. !== false --> linking will be enabled
 * @constructor
 */
var CommonMap = function (linkItems) {
    this.current = undefined;
    this.size = 0;

    if(linkItems === false)
        this.disableLinking();
}

CommonMap.noop = function() {
    return this;
};

CommonMap.illegal = function() {
    throw new Error("illegal operation for maps without linking");
};

/**
 * Map initialisation from existing object
 * doesn't add inherited properties if not explicitly instructed to:
 * omitting foreignKeys means foreignKeys === undefined, i.e. == false --> inherited properties won't be added
 * @param {Object} obj value
 * @param {Object|undefined} foreignKeys key
 * @return {Object} Created map
 */
CommonMap.from = function(obj, foreignKeys) {
    var map = new CommonMap;

    for(var prop in obj) {
        if(foreignKeys || obj.hasOwnProperty(prop))
            map.put(prop, obj[prop]);
    }

    return map;
};

/**
 * Creates disable linking
 * @return {Object} Created struct
 */
CommonMap.prototype.disableLinking = function() {
    this.link = Map.noop;
    this.unlink = Map.noop;
    this.disableLinking = Map.noop;
    this.next = Map.illegal;
    this.key = Map.illegal;
    this.value = Map.illegal;
    this.removeAll = Map.illegal;

    return this;
};

/**
 * Gets hash values of object
 * @param {Object} value object to research
 * @return {string} hash values of object
 */
CommonMap.prototype.hash = function(value) {
    return (typeof value) + ' ' + (value instanceof Object ?
        (value.__hash || (value.__hash = ++arguments.callee.current)) :
        value.toString());
};

CommonMap.prototype.hash.current = 0;

/**
 * Gets values of object by key
 * @param {String} key for look up
 * @return {string|Object} values
 */
CommonMap.prototype.get = function(key) {
    var item = this[this.hash(key)];
    return item === undefined ? undefined : item.value;
};

/**
 * Puts key value to map
 * @param {String} key for look up
 * @param {String} value
 * @return {Object} Map
 */
CommonMap.prototype.put = function(key, value) {
    var hash = this.hash(key);
    if(this[hash] === undefined) {
        var item = { key : key, value : value };
        this[hash] = item;

        this.link(item);
        ++this.size;
    }
    else this[hash].value = value;

    return this;
};

/**
 * Removes values of object by key
 * @param {String} key for look up
 * @return {Object} Map
 */
CommonMap.prototype.remove = function(key) {
    var hash = this.hash(key);
    var item = this[hash];

    if(item !== undefined) {
        --this.size;
        this.unlink(item);

        delete this[hash];
    }

    return this;
};

/**
 * Removes all key values in map
 * only works if linked
 * @return {Object} Map
 */
CommonMap.prototype.removeAll = function() {
    while(this.size)
        this.remove(this.key());

    return this;
};


// --- linked list helper functions
/**
 * Links item
 * @param {Object} item object to link
 */
CommonMap.prototype.link = function(item) {
    if(this.size == 0) {
        item.prev = item;
        item.next = item;
        this.current = item;
    }
    else {
        item.prev = this.current.prev;
        item.prev.next = item;
        item.next = this.current;
        this.current.prev = item;
    }
};

/**
 * Unlinks item
 * @param {Object} item object to unlink
 */
CommonMap.prototype.unlink = function(item) {
    if(this.size == 0)
        this.current = undefined;
    else {
        item.prev.next = item.next;
        item.next.prev = item.prev;
        if(item === this.current)
            this.current = item.next;
    }
};

// --- iterator functions - only work if map is linked

/**
 * Iterator to move to next object.
 */
CommonMap.prototype.next = function() {
    this.current = this.current.next;
};

/**
 * Gets key
 * @return {String} current key
 */
CommonMap.prototype.key = function() {
    return this.current.key;
};

/**
 * Gets value
 * @return {Object} current value
 */
CommonMap.prototype.value = function() {
    return this.current.value;
};

module.exports = CommonMap;