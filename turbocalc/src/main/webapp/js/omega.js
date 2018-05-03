//! ommga.js - The end-all-be-all, fun to use, most sarcastic of JavaScript libraries
//! version : -0.01 * e^(π*i)
//! authors : Mitchell Mebane
//! license : CC0

// N.B.: Ω is ALT+234 on Windows
// or Option-Z or something on Mac

/* global HTMLTemplateElement: false */

/**
* @class Ω
* @property {Window}          win - Shorthand for <code>window</code>.
* @property {HTMLDocument}    doc - Shorthand for <code>document</code>.
* @property {HTMLHtmlElement} html - The <code>&lt;html&gt;</code> element
* @property {HTMLHeadElement} head - The <code>&lt;head&gt;</code> element  Shorthand for <code>document.head</code>.
* @property {HTMLBodyElement} body - The <code>&lt;body&gt;</code> element.  Shorthand for <code>document.body</code>.
*/
(function () {
    var _oldΩ = window.Ω;
    
    var _win = window, 
        _doc = document, 
        _html = _doc.documentElement, 
        _head = _doc.head, 
        _body = _doc.body;
    
    
    /**
     * @method id
     * @memberof Ω
     * @param {String} id - A case-sensitive string representing the unique ID of the element being sought.
     * @returns {Element} The element with the given <code>id</code>, or <code>null</code> if no such element was found.
     * @description Shorthand for <code>document.getElementById</code>
     */
    var _id = _doc.getElementById.bind(_doc);
    
    /**
     * @method sel
     * @memberof Ω
     * @param {String} selector - A string containing one or more CSS selectors, separated by commas.
     * @param {Element} [rootEl=document] - Element to use as the base for the CSS query.  If not given, will default to <code>document</code>.
     * @returns {Element} The first element to match the given <code>selector</code>, or <code>null</code> if no such element was found.
     * @description Returns the first element that is a descendant of the given root element 
     *     and that matches the specified group of selectors.
     */
    var _sel = function(selector, rootEl) {
        return (rootEl ? rootEl : _doc).querySelector(selector);
    };
    
    /**
     * @method selAll
     * @memberof Ω
     * @param {String} selector - A string containing one or more CSS selectors, separated by commas.
     * @param {Element} [rootEl=document] - Element to use as the base for the CSS query.  If not given, will default to <code>document</code>.
     * @returns {NodeList} Non-live <code>NodeList</code> containing the elements which match the given <code>selector</code>.  
     *     If no such elements are found, the <code>NodeList</code> will be empty.
     * @description Returns all elements that are descendants of the given root element 
     *     and that match the specified group of selectors.
     */
    var _selAll = function(selector, rootEl) {
        var resNodeList = (rootEl ? rootEl : _doc).querySelectorAll(selector);
        resNodeList.filter = Array.prototype.filter.bind(resNodeList);
        resNodeList.forEach = Array.prototype.forEach.bind(resNodeList);
        resNodeList.map = Array.prototype.map.bind(resNodeList);
        return resNodeList;
    };
    
    
    /**
     * @method parent
     * @memberof Ω
     * @param {Element} node - The element to start at when searching for a parent.  The element must not be window/document/html/body.
     * @param {String} selector - A CSS selector which defines the desired parent.
     * @param {Boolean} includeSelf - <code>true</code> if the element itself should be considered when searching.  <code>false</code> 
     *     if only proper parents should be considered.
     * @returns {Element} An element which matches the given selector and is a parent of the given start node, or <code>undefined</code> if no 
     *     such parent could be found.  The search is considered finished when it has reached either a) an element whose parent is undefined, or b) 
     *     the <code>body</code> element.
     * @description Searches upwards through an element's parents to find an element matching a given selector.
     */
   var _parent = function(node, selector, includeSelf) {
        if(!node || !selector) {
            throw new Error("node and selector must be given");
        }
        if(node === _win || node === _doc || node === _html || node === _body) {
            throw new Error("invalid choice of node");
        }
        var p = (includeSelf ? node : node.parentNode);
        while(!!p && p !== _body) {
            if(p.matches(selector)) {
                return p;
            }
            p = p.parentNode;
        }
        return undefined;
    };
    
    
    /**
     * @method on
     * @memberof Ω
     * @param {EventTarget} el - The element to which to add an event listener.
     * @param {String} ev - The event type for which to listen (e.g., <code>click</code> or <code>keypress</code>).
     * @param {Function} handler - The function called when the given event fires.  
     *     The handler will be passed one parameter, an <code>Event</code> object.
     * @param {Boolean} useCapture - <code>true</code> if event handling should start here, 
     * <code>false</code> if it should start at children.
     * @description Shorthand for <code>addEventListener</code>
     */
    var _on = function(el, ev, handler, useCapture) {
        return el.addEventListener(ev, handler, !!useCapture);
    };
    
    /**
     * @method make
     * @memberof Ω
     * @param {String} tagName - Type of the element which is to be created (e.g., <code>div</code>).
     * @returns {Element} A new HTML element of the specified type, or <code>HTMLUnknownElement</code> if the element type is not known.
     * @description Shorthand for <code>document.createElement</code>.
     */
    var _make = _doc.createElement.bind(_doc);
    
    /**
     * @method makeText
     * @memberof Ω
     * @param {String} data - The data to put into the new text node.
     * @returns {Text} A new text node containing the given text.
     * @description Shorthand for <code>document.createTextNode</code>.
     */
    var _makeText = _doc.createTextNode.bind(_doc);
    
    /**
     * @method frag
     * @memberof Ω
     * @param {(String|HTMLTemplateElement|Element)} content - The content to insert into the new fragment.
     * @param {Boolean} [cloneEls=false] - (Only applies if the <code>content</code> parameter is an <code>HTMLElement</code>).
     *     If true, the contents of the given element will be cloned.  If <code>false</code>, the contents will be adopted.
     *     (If <code>content</code> is an <code>HTMLTemplateElement</code>, the contents will <em>always</em> be cloned.)
     * @returns {DocumentFragment} The content of the fragment will vary depending on the type of the <code>content</code> parameter:
     *      <dl>
     *          <dt>undefined</dt>
     *          <dd>The returned fragment will contain no content.</dd>
     *          <dt>String</dt>
     *          <dd>
     *              The returned fragment will contain all nodes resulting from parsing <code>content</code> as HTML.  
     *              This should be assumed to be an unsafe parse - i.e., do not pass untrusted HTML to this method.
     *          </dd>
     *          <dt>HTMLTemplateElement</dt>
     *          <dd>The returned fragment will contain a single child, a clone of the template's <code>content</code> attribute.</dd>
     *          <dt>Element</dt>
     *          <dd>
     *              For an <code>Element</code> that is not an <code>HTMLTemplateElement</code>, the returned fragment will 
     *              contain a single child, the passed-in <code>content</code>.  If <code>clone</code> is true, 
     *              this will have been cloned via <code>cloneNode</code>.  Otherwise, the given element will be adopted.
     *          </dd>
     *      </dl>
     * @description Creates a <code>DocumentFragment</code> containing the given content.
     */
    var _frag = function(content, cloneEls) {
        var frag = _doc.createDocumentFragment();
        if(!content) {
            // no content - just return an empty fragment
        }
        else if(_isString(content)) {
            var tmp = _make("div"), 
                el;
            tmp.innerHTML = content;
            while(el = tmp.firstChild) {
                frag.appendChild(el);
            }
        }
        else if(content instanceof HTMLTemplateElement) {
            // templates will get cloned
            frag.appendChild(content.content.cloneNode(true));
        }
        else if(content instanceof Element) {
            if(cloneEls) {
                frag.appendChild(content.cloneNode(true));
            }
            else {
                frag.appendChild(content);
            }
        }
        return frag;
    };
    
    /**
     * @method prependChild
     * @memberof Ω
     * @param {Element} parent - The reference element.
     * @param {Element} newChild - The new element to insert into the DOM before the reference element.
     * @returns {Element} The element which was inserted.
     * @description Inserts a new element into the DOM at the same level as, and immediately preceding, a given anchor element
     */
    var _prependChild = function(parent, newChild) {
        return parent.insertBefore(newChild, parent.firstChild);
    };
    
    /**
     * @method appendSibling
     * @memberof Ω
     * @param {Element} parent - The reference element.
     * @param {Element} newChild - The new element to insert into the DOM before the reference element.
     * @returns {Element} The element which was inserted.
     * @description Inserts a new element into the DOM at the same level as, and immediately after, a given anchor element.
     */
    var _appendChild = function(parent, newChild) {
        return parent.appendChild(newChild);
    };
    
    /**
     * @method prependSibling
     * @memberof Ω
     * @param {Element} anchor - The reference element.
     * @param {Element} newSibling - The new element to insert into the DOM before the reference element.
     * @returns {Element} The element which was inserted.
     * @description Inserts a new element into the DOM at the same level as, and immediately preceding, a given anchor element
     */
    var _prependSibling = function(anchor, newSibling) {
        return anchor.parentNode.insertBefore(newSibling, anchor);
    };
    
    /**
     * @method appendSibling
     * @memberof Ω
     * @param {Element} anchor - The reference element.
     * @param {Element} newSibling - The new element to insert into the DOM before the reference element.
     * @returns {Element} The element which was inserted.
     * @description Inserts a new element into the DOM at the same level as, and immediately after, a given anchor element.
     */
    var _appendSibling = function(anchor, newSibling) {
        return anchor.parentNode.insertBefore(newSibling, anchor.nextSibling);
    };
    
    /**
     * @method clear
     * @memberof Ω
     * @param {Node} toClear - The element from which to remove all children.
     * @description Removes all child nodes from the given node.
     */
    var _clear = function(toClear) {
        var i;
        while(i = toClear.lastChild) {
            toClear.removeChild(i);
        }
    };
    
    /**
     * @method remove
     * @memberof Ω
     * @param {Node} toRemove - The node to remove from the DOM.
     * @returns {Node} The node that was removed.
     * @description Removes a node from the DOM.
     */
    var _remove = function(toRemove) {
        return toRemove.parentNode.removeChild(toRemove);
    };
     
    
    /**
     * @method addDefaults
     * @memberof Ω
     * @param {Object} obj - The object to which defaults will be added.  If any properties are added, this object <em>will</em> be modified.
     * @param {Object} defaults - The object from which to load defaults.
     * @returns {Object} <code>obj</code>, with any added properties.  This will be the same reference 
     *     as the passed-in <code>obj</code>.
     * @description Augments a given object with properties from a "defaults" object.  Only properties which are 
     *     not already present in <code>obj</code> will be added&mdash;if a property is present in both <code>obj</code> 
     *     and <code>defaults</code>, the copy in <code>obj</code> will not be modified.<br>
     *     <br>
     *     Only own properties of <code>defaults</code> will be considered.
     */
    var _addDefaults = function(obj, defaults) {
        Object.keys(defaults).forEach(function(key) {
            //if(!obj.hasOwnProperty(key)) {
            // Don't allow defaults to overwrite inherited properties
            if(!(key in obj)) {
                obj[key] = defaults[key];
            }
        });
        
        return obj;
    };
    
    /**
     * @method xhr
     * @memberof Ω
     * @param {String} method - An HTTP request method.  (E.g., <code>GET</code> or <code>POST</code>.)
     * @param {String} url - The URL to which to make the request
     * @param {Object} reqBodyObject - An object to send in the body of the request.  If given, it will 
     *     be converted to a JSON string before sending.  (If the object is already a string, it will be 
     *     assumed to be valid JSON and sent as-is.)  If you do not wish to send a body in the request, 
     *     <code>undefined</code> or <code>null</code> should be passed for this parameter.
     * @param {Function} onload - Callback function invoked when the request completes successfully.  
     *     It will be passed one parameter, the <code>XMLHttpRequest</code> object.
     * @param {Function} [onerror] - Callback function invoked when the request completes successfully.  
     *     It will be passed one parameter, the <code>XMLHttpRequest</code> object.
     * @returns The in-flight XMLHttpRequest object.
     * @description Helper method for common usages of XMLHttpRequest
     */
    var _xhr = function(method, url, reqBodyObject, onload, onerror) {
        var req = new XMLHttpRequest();
        req.onload = function() {
            onload(req);
        };
        if(onerror) {
            req.onerror = function() {
                onerror(req);
            };
        }
        req.open(method, url, true);
        if(reqBodyObject) {
            req.setRequestHeader("Content-Type", "application/json");
            if(_isString(reqBodyObject)) {
                req.send(reqBodyObject);
            }
            else {
                req.send(JSON.stringify(reqBodyObject));
            }
        }
        else {
            req.send();
        }
        
        return req;
    };
    
    var _isNullOrUndefined = function(obj) {
        return (null === obj || undefined === obj);
    }
    
    var _isString = function(obj) {
        if(_isNullOrUndefined(obj)) {
            return false;
        }
        return (obj.constructor === String || obj instanceof String);
    };
    
    var _toArray = function(obj) {
        return [].map.call(obj, function(element) {
            return element;
        })
    };
    
    
    /**
     * @method noConflict
     * @memberof Ω
     * @returns {Ω} The omega.js instance that was in window.Ω before <code>noConflict</code> was called.
     * @description Restores the value of window.Ω to whatever it was before this script was executed.
     */
    var _noConflict = function() {
        var omega = window.Ω;
        window.Ω = _oldΩ;
        return omega;
    };
    
    
    var Ω = {
        win: _win, 
        doc: _doc, 
        html: _html, 
        head: _head, 
        body: _body, 
        id: _id, 
        sel: _sel, 
        selAll: _selAll, 
        parent: _parent, 
        on: _on, 
        make: _make, 
        makeText: _makeText, 
        frag: _frag, 
        prependChild: _prependChild, 
        appendChild: _appendChild, 
        prependSibling: _prependSibling, 
        appendSibling: _appendSibling, 
        clear: _clear, 
        remove: _remove, 
        addDefaults: _addDefaults, 
        xhr: _xhr, 
        isNullOrUndefined: _isNullOrUndefined, 
        isString: _isString, 
        toArray: _toArray, 
        noConflict: _noConflict
    };
    
    window.Ω = {};
    Object.keys(Ω).forEach(function(key) {
        Object.defineProperty(window.Ω, key, {
            enumerable: true, 
            configurable: false, 
            writable: false, 
            value: Ω[key]
        });
    });
})();
