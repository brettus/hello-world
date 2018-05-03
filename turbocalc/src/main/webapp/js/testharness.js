//! Test harness for Turbocalendulator REST services
//! version : 0.1
//! authors : Mitchell Mebane
//! license : CC0

/* global Ω: false, LiteBox: false, moment: false, Handlebars: false */

var TestHarness = (function() {
    var _eventTemplate, 
        _outputContainer;
    
    var _validate = function(els) {
        // TODO: Would be better with event delegation
        var args = Array.prototype.slice.call(arguments);
        args.forEach(function(el) {
            if(!(el && el instanceof HTMLInputElement)) {
                return;
            }
            var formatString = el.getAttribute('data-formatString');
            if(!formatString) {
                return;
            }
            
            var r = new RegExp(formatString);
            Ω.on(el, 'input', function(ev) {
                var el = ev.target;
                if(r.test(el.value)) {
                    el.classList.remove("invalid");
                }
                else {
                    el.classList.add("invalid");
                }
            });
            
            // prime the validation
            var event = Ω.doc.createEvent("UIEvents");
            event.initUIEvent("input", true, true, window, 1);
            el.dispatchEvent(event);
        });
    };
    
    var _logEvent = function(calendarEvent) {
        _outputContainer.innerHTML += _eventTemplate(calendarEvent);
    };
    
    var _getServiceURL = function(service) {
        return Ω.id('serviceBaseURL').value + service;
    };
    
    var _listAll = function() {
        _clear();
        Ω.xhr("GET", _getServiceURL("/event/all"), undefined, function(req) {
            if(req.status == 200) {
                var resp = req.responseText;
                try {
                    console.log(resp);
                    
                    var events = JSON.parse(resp), 
                        numEvents = events.events.length;
                    for(var i = 0; i < numEvents; ++i) {
                        var ev = events.events[i];
                        if(ev) {
                            _logEvent(ev);
                        }
                    }
                }
                catch (e) {
                    alert("Invalid response: \n" + resp);
                }
            }
            else {
                alert("Bad response: " + resp.status);                
            }
        });
    };
    
    var _tagSearch = function() {
        var tagSearchMarkup = Ω.frag(Ω.id("tag-search-input-template")), 
            tagSearchBox = new LiteBox();
            
        var tagSearchForm = Ω.sel("form", tagSearchMarkup), 
            btnCancel = Ω.sel("#btnCancel", tagSearchForm), 
            btnSubmit = Ω.sel("#btnSubmit", tagSearchForm);
            
        Ω.on(btnCancel, 'click', function(ev) {
            tagSearchBox.dispose();
            ev.preventDefault();
            ev.stopPropagation();
        });
        
        Ω.on(tagSearchForm, 'submit', function(ev) {
            var tags = Ω.sel('[name=tags]', tagSearchForm).value;
                
            var tagParam = tags
                .split(/[;,]/)
                .map(function(t){return t.replace(/^\s+|\s+$/g, '');})
                .join("+");
            
            console.log("Searching for tags: " + tagParam);
            
            Ω.xhr("GET", _getServiceURL("/event/tag/" + tagParam), null, function(req) {
                Ω.id('output').innerHTML = "";
                var resp = req.responseText;
                try {
                    console.log(resp);
                    
                    var events = JSON.parse(resp), 
                        numEvents = events.events.length;
                    for(var i = 0; i < numEvents; ++i) {
                        var ev = events.events[i];
                        if(ev) {
                            _logEvent(ev);
                        }
                    }
                }
                catch (e) {
                    alert("Invalid response: \n" + resp);
                }
            });
            
            tagSearchBox.dispose();
            ev.preventDefault();
            ev.stopPropagation();
        });
        
        tagSearchBox.show(tagSearchMarkup);
    };
    
    var _dateSearch = function() {
        var dateSearchMarkup = Ω.id("date-search-input-template").content.cloneNode(true), 
            dateSearchBox = new LiteBox();
            
        var dateSearchForm = Ω.sel("form", dateSearchMarkup), 
            btnCancel = Ω.sel("#btnCancel", dateSearchForm), 
            btnSubmit = Ω.sel("#btnSubmit", dateSearchForm);
        
        var startYear  = Ω.sel('[name=startYear]', dateSearchForm), 
            startMonth = Ω.sel('[name=startMonth]', dateSearchForm), 
            startDay   = Ω.sel('[name=startDay]', dateSearchForm), 
            endYear    = Ω.sel('[name=endYear]', dateSearchForm), 
            endMonth   = Ω.sel('[name=endMonth]', dateSearchForm), 
            endDay     = Ω.sel('[name=endDay]', dateSearchForm);
        
        _validate(startYear, startMonth, startDay, endYear, endMonth, endDay);
            
        Ω.on(btnCancel, 'click', function(ev) {
            dateSearchBox.dispose();
            ev.preventDefault();
            ev.stopPropagation();
        });
        
        Ω.on(dateSearchForm, 'submit', function(ev) {
            var dateString = "/from/" + startYear.value + "/" + startMonth.value;
            if(startDay.value) {
                dateString = dateString + "/" + startDay.value;
            }
            dateString = dateString + "/to/" + endYear.value + "/" + endMonth.value;
            if(endDay.value) {
                dateString = dateString + "/" + endDay.value;
            }
            console.log(dateString);
            
            Ω.xhr("GET", _getServiceURL("/event" + dateString), null, function(req) {
                _clear();
                var resp = req.responseText;
                try {
                    console.log(resp);
                    
                    var events = JSON.parse(resp), 
                        numEvents = events.events.length;
                    for(var i = 0; i < numEvents; ++i) {
                        var ev = events.events[i];
                        if(ev) {
                            _logEvent(ev);
                        }
                    }
                }
                catch (e) {
                    alert("Invalid response: \n" + resp);
                }
            });
            
            dateSearchBox.dispose();
            ev.preventDefault();
            ev.stopPropagation();
        });
        
        dateSearchBox.show(dateSearchMarkup);
    };
    
    var _reset = function(onSuccess, onFailure) {
        Ω.xhr("GET", _getServiceURL("/admin/resetmemdb"), undefined, function(req) {
            if(req.status == 200) {
                console.log("DB Reset");
                if(onSuccess) {
                    onSuccess();
                }
            }
            else {
                console.log("Error resetting DB");
                if(onFailure) {
                    onFailure();
                }
            }
        });
    };
    
    var _clear = function() {
        Ω.id('output').innerHTML = "";
    };
    
    var _add = function() {
        var addEvent = Ω.id("add-event-template").content.cloneNode(true), 
            addBox = new LiteBox();
            
        var _form = Ω.sel("form", addEvent), 
            _cancel = Ω.sel("#btnCancel", _form), 
            _submit = Ω.sel("#btnSubmit", _form);
            
        Ω.on(_cancel, 'click', function(ev) {
            addBox.dispose();
            ev.preventDefault();
            ev.stopPropagation();
        });
        
        Ω.on(_form, 'submit', function(ev) {
            var startDate =      Ω.sel('[name=startDate]',      _form).value, 
                startTime =      Ω.sel('[name=startTime]',      _form).value, 
                endDate =        Ω.sel('[name=endDate]',        _form).value, 
                endTime =        Ω.sel('[name=endTime]',        _form).value, 
                description =    Ω.sel('[name=description]',    _form).value, 
                location =       Ω.sel('[name=location]',       _form).value, 
                notes =          Ω.sel('[name=notes]',          _form).value, 
                tags =           Ω.sel('[name=tags]',           _form).value, 
                otherAttendees = Ω.sel('[name=otherAttendees]', _form).value;
                
            var tagArray = tags
                .split(/[;,]/)
                .map(function(t){return t.replace(/^\s+|\s+$/g, '');});
            var otherAttendeesArray = otherAttendees
                .split(/[;,]/)
                .map(function(t){return t.replace(/^\s+|\s+$/g, '');});
                
            var event = {
                "start_datetime": moment(startDate + " " + startTime).format(), 
                "end_datetime": moment(endDate + " " + endTime).format(), 
                "description": description, 
                "location": location, 
                "notes": notes, 
                "tags": tagArray, 
                "other_attendees": otherAttendeesArray
            };
            
            console.log(JSON.stringify(event));
            
            Ω.xhr("POST", _getServiceURL("/event"), event, function(req) {
                if(req.status == 201) {
                    console.log("Event Created");
                    console.log(req.getResponseHeader("Location"));
                }
                else {
                    console.log("Error adding event");
                }
            });
            
            addBox.dispose();
            ev.preventDefault();
            ev.stopPropagation();
        });
        
        addBox.show(addEvent);
    };
    
    var _export = function() {
        Ω.xhr("GET", _getServiceURL("/admin/export"), undefined, function(req) {
            if(req.status == 200) {
                var resultsTemplate = Ω.id("export-events-template").content.cloneNode(true), 
                    resultsBox = new LiteBox({
                        closeOnClickOut: true
                    });
                
                var btnSelectAll = Ω.sel("#btnSelectAll", resultsTemplate), 
                    textContainer = Ω.sel(".textContainer", resultsTemplate);
                
                var resp = req.responseText, 
                    respText;
                
                try {
                    respText = JSON.stringify(JSON.parse(resp), undefined, "    ");
                }
                catch (e) {
                    alert("Invalid response: \n" + resp);
                    return;
                }
                
                Ω.on(btnSelectAll, "click", function(ev) {
                    textContainer.select();
                });
                
                Ω.sel(".textContainer", resultsTemplate).appendChild(Ω.doc.createTextNode(respText));
                resultsBox.show(resultsTemplate);
            }
        });
    };
    
    var _import = function() {
        var importTemplate = Ω.id("import-events-template").content.cloneNode(true), 
            importBox = new LiteBox({
                closeOnClickOut: true
            });
        
        var importForm = Ω.sel("form", importTemplate), 
            btnCancel = Ω.sel("#btnCancel", importForm), 
            btnSubmit = Ω.sel("#btnSubmit", importForm);
        
        Ω.on(btnCancel, 'click', function(ev) {
            importBox.dispose();
            ev.preventDefault();
            ev.stopPropagation();
        });
        
        Ω.on(importForm, 'submit', function(ev) {
            var data = JSON.parse(Ω.sel(".textContainer", importForm).value);
            
            Ω.xhr("POST", _getServiceURL("/admin/import"), data, function(req) {
                if(req.status == 201) {
                    console.log("Event Created");
                    console.log(req.getResponseHeader("Location"));
                }
                else {
                    console.log("Error importing events");
                    alert("Error importing events");
                }
            });
            
            importBox.dispose();
            ev.preventDefault();
            ev.stopPropagation();
        });
        
        importBox.show(importTemplate);
    };
    
    var _init = function() {
        Ω.on(Ω.id('btnListAll'), 'click', function(ev) {
            _listAll();
        });
        
        Ω.on(Ω.id('btnTagSearch'), 'click', function(ev) {
            _tagSearch();
        });
        
        Ω.on(Ω.id('btnDateSearch'), 'click', function(ev) {
            _dateSearch();
        });
        
        Ω.on(Ω.id('btnAddEvent'), 'click', function(ev) {
            _add();
        });
        
        Ω.on(Ω.id('btnClear'), 'click', function(ev) {
            _clear();
        });
        
        Ω.on(Ω.id('btnReset'), 'click', function(ev) {
            _reset();
        });
        
        Ω.on(Ω.id('btnExport'), 'click', function(ev) {
            _export();
        });
        
        Ω.on(Ω.id('btnImport'), 'click', function(ev) {
            _import();
        });
        
        _eventTemplate = Handlebars.compile(Ω.id("event-template").innerHTML);
        _outputContainer = Ω.id("output");
        
        Ω.id('serviceBaseURL').value = window.location.origin + "/services";
    };

    return {
        init: _init
    };
})();

TestHarness.init();