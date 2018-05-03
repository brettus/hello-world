<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Turbocalendulator Service Test Harness</title>
    <link rel="stylesheet" href="css/testharness.css">
    <link rel="stylesheet" href="css/LiteBox.css">
</head>

<body>
    Functions: 
    <ul id="actionBar">
        <li><button id="btnListAll">List All</button></li>
        <li><button id="btnTagSearch">Search By Tag</button></li>
        <li><button id="btnDateSearch">Search By Date Range</button></li>
        <li><button id="btnAddEvent">Add Event</button></li>
        <li><button id="btnClear">Clear</button></li>
        <li><button id="btnReset">Reset memDB</button></li>
        <li><button id="btnExport">Export data</button></li>
        <li><button id="btnImport">Import data</button></li>
    </ul>
    <div>
        <label for="serviceBaseURL">Service base URL: </label><input type="text" name="serviceBaseURL" id="serviceBaseURL" size="64">
    </div>
    
    <span>Output:</span>
    <div id="output">
    </div>
    
    <template id="event-template">
        <div class="event">
            <div id="eventId"><span class="fieldLabel">ID:</span> {{id}}</div>
            <div id="startTime"><span class="fieldLabel">Starts at:</span> {{start_datetime}}</div>
            <div id="endTime"><span class="fieldLabel">Ends at:</span> {{end_datetime}}</div>
            <div id="description"><span class="fieldLabel">Description:</span> {{description}}</div>
            <div id="location"><span class="fieldLabel">Location:</span> {{location}}</div>
            <div id="notes"><span class="fieldLabel">Notes:</span> {{notes}}</div>
            <div id="tags"><span class="fieldLabel">Tags:</span> {{#each tags}}{{#unless @first}}, {{/unless}}{{this}}{{/each}}</div>
            <div id="otherAttendees"><span class="fieldLabel">Attendees:</span> {{other_attendees}}</div>
        </div>
    </template>
    
    <template id="add-event-template">
        <form class="add-event gridForm">
            <div class="form-items">
                <div class="form-item">
                    <label for="startDate">Start Date:</label> <input name="startDate" type="date" placeholder="YYYY-MM-DD">
                </div>
                <div class="form-item">
                    <label for="startTime">Start Time:</label> <input name="startTime" type="time" placeholder="HH:MM">
                </div>
                <div class="form-item">
                    <label for="endDate">End Date:</label> <input name="endDate" type="date" placeholder="YYYY-MM-DD">
                </div>
                <div class="form-item">
                    <label for="endTime">End Time:</label> <input name="endTime" type="time" placeholder="HH:MM">
                </div>
                <div class="form-item">
                    <label for="description">Description:</label> <input name="description" type="text">
                </div>
                <div class="form-item">
                    <label for="location">Location:</label> <input name="location" type="text">
                </div>
                <div class="form-item">
                    <label for="notes">Notes:</label> <input name="notes" type="text">
                </div>
                <div class="form-item">
                    <label for="tags">Tags:</label> <input name="tags" type="text" placeholder="tag1; tag2; tag3">
                </div>
                <div class="form-item">
                    <label for="otherAttendees">Other Attendees:</label> <input name="otherAttendees" type="text" placeholder="x@y.com; a@b.com">
                </div>
            </div>
            
            <button id="btnCancel">Cancel</button> <input id="btnSubmit" type="submit" value="Add Event">
        </form>
    </template>
    
    <template id="export-events-template">
        <div style="display: inline-block;">
            <textarea class="textContainer" style="height: 50ex; margin-bottom: 2em; padding: 1em; text-align: left; width: 120ex;"></textarea>
        </div>
        <div class="dialogControls">
            <button id="btnSelectAll">Select All</button>
        </div>
    </template>
    
    <template id="import-events-template">
        <form class="import-events">
            <div>
                <textarea class="textContainer" style="height: 50ex; margin-bottom: 2em; padding: 1em; text-align: left; width: 120ex;"></textarea>
            </div>
            
            <button id="btnCancel">Cancel</button> <input id="btnSubmit" type="submit" value="Import Events">
        </form>
    </template>
    
    <template id="tag-search-input-template">
        <form class="tag-search-input gridForm">
            <div class="form-items">
                <div class="form-item">
                    <label for="tags">Tags:</label> <input name="tags" type="text" placeholder="tag1; tag2; tag3">
                </div>
            </div>
            
            <button id="btnCancel">Cancel</button> <input id="btnSubmit" type="submit" value="Search">
        </form>
    </template>
    
    <template id="date-search-input-template">
        <form class="date-search-input gridForm">
            <div class="form-items">
                <div class="form-item">
                    <label for="startYear">Start Year:</label> <input name="startYear" type="text" placeholder="YYYY" data-formatString="^\d{1,6}$">
                </div>
                <div class="form-item">
                    <label for="startMonth">Start Month:</label> <input name="startMonth" type="text" placeholder="MM" data-formatString="^\d{1,2}$">
                </div>
                <div class="form-item">
                    <label for="startDay">Start Day:</label> <input name="startDay" type="text" placeholder="DD" data-formatString="^\d{0,2}$">
                </div>
                <div class="form-item">
                    <label for="endYear">end Year:</label> <input name="endYear" type="text" placeholder="YYYY" data-formatString="^\d{1,6}$">
                </div>
                <div class="form-item">
                    <label for="endMonth">end Month:</label> <input name="endMonth" type="text" placeholder="MM" data-formatString="^\d{1,2}$">
                </div>
                <div class="form-item">
                    <label for="endDay">end Day:</label> <input name="endDay" type="text" placeholder="DD" data-formatString="^\d{0,2}$">
                </div>
            </div>
            
            <button id="btnCancel">Cancel</button> <input id="btnSubmit" type="submit" value="Search">
        </form>
    </template>
    
    <script src="js/handlebars-v1.3.0-min.js"></script>
    <script src="js/moment-with-locales-2.8.3.min.js"></script>
    <script src="js/moment-timezone-with-data-0.2.2-2014g.min.js"></script>
    <script src="js/omega.js"></script>
    <script src="js/LiteBox.js"></script>
    <script src="js/testharness.js"></script>
</body>
</html>