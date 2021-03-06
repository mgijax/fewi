# HMDC Documentation

## JSP
### [home.jsp](www/WEB-INF/jsp/hmdc/home.jsp)
This is the main file that is used for the HMDC home page. This page includes all the standard header and footer. 

## CSS
### [search.css](www/css/hmdc/search.css)
This is the main style sheet for the page. Most all styles can be found in here.

### [popup.css](www/css/hmdc/popup.css)
This style sheet controls the styles in the popup windows from the hmdc. This is also included on the main page of the HMDC.

## Controllers
### [DiseaseController](www/hmdc/app/components/search/DiseaseController.js)
This controller handles the filtering for the disease table, as well as using the Search Service to do the data query for the table. This controller also handles the popup for the "Source" link on the smart table.
### [GeneController](www/hmdc/app/components/search/GeneController.js)
This controller handles the filtering for the gene table, as well as using the Search Service to do the data query for the table. This controller also handles the popup for the "Source" link on the smart table.
### [GridController](www/hmdc/app/components/search/GridController.js)
This controller handles the following:

  1. Popups
  2. Custom methods for the grid formating on the ng-cells directive
  3. Grid filtering
  4. Gird Search
  5. Updates the amount of rows and columns to show based on window size
  6. Builds out the filter lists for all the tabs
  7. Filters / Builds the grid.
  8. Changes the JSON String coming from Fewi into the format needed for the ng-cells grid

### [SearchController](www/hmdc/app/components/search/SearchController.js)
This controller handles the following:

  1. Setting up the filter settings options
  2. The cooridnation of events between the tabs for the filters
  3. The "You Searched For" text
  4. The click on the search buttton from the main query form
  5. Parses the gene upload file.
  6. Definition of the tabs
  7. Definition of the query form

## Services
### [AutoCompleteService](www/hmdc/app/components/search/AutoCompleteService.js)
This service provides a method for getting the REST endpoint for the autocomplete controller, for HMDC. This method takes the data in the query form in order to process the search to solr.
### [SearchService](www/hmdc/app/components/search/SearchService.js)
This service provides three methods used for doing Ajax calls to the server for each of the three tabs. All of them take the formData from the query form and pass that data to the server for processing.
### [NaturalSortService](www/hmdc/app/components/search/NaturalSortService.js)
This service provides a method that can be used to "natural sort" the list that is passed to it. This might be better known as Smart Alpha sorting.

## Directives
### [SearchStResetDirective](www/hmdc/app/components/search/SearchStResetDirective.js)
This directive is used to add to the different tables in order to get the "reset" functionality for when the table data changes.

## Filters
Filters are used to remove results from the query. Filters do not go back to the server with parameters. Filters filter out what is not selecte and keep what is selected. Also the filter list are not facet queries but rather lists from the original query.
### [OrderHashByFilter]( www/hmdc/app/components/search/OrderHashByFilter.js)
This filter is used on different columns in the smart tables to order by that field of data by chaining it by a |
### [SearchHandleSubscriptFilter](www/hmdc/app/components/search/SearchHandleSubscriptFilter.js)
This filter is used to handle sub scripting on different fields, by adding to the field via a |
### [SearchSortFilter](www/hmdc/app/components/search/SearchSortFilter.js)
This filter is used to sort the columns of the smart tables by the header of the column. When clicking on the column heading the sort will reverse


## Templates - HTML
### [searchTemplate.html](www/hmdc/app/components/search/searchTemplate.html)
This file is used to display the search query form, and all the tabs that are driven by the model in the search controller.
### [diseaseTemplate.html](www/hmdc/app/components/search/diseaseTemplate.html)
This file is used to display the disease table. It makes use of angular Smart Table
### [geneTemplate.html](www/hmdc/app/components/search/geneTemplate.html)
This file is used to display the gene table. It makes use of angular Smart Table
### [gridTemplate.html](www/hmdc/app/components/search/gridTemplate.html)
This file is used to display the HMDC grid. It makes use of angular ng-cells module.

## Flow Control

### What happens when the search button is clicked?

#### Search Controller
  1. vm.onSubmit() on Search Controller is run
  2. Clears the loaded file data from the model before sending it on.
  3. Emits the CallSearchMethod Event which goes to all three controllers (Grid, Gene, Disease)
  4. Hides the query form
  5. Generates "You Searched for" string

#### Grid Controller
  1. Sets the grid loading to true
  2. Runs the Ajax search method
  3. Builds Filter Lists in the root scope
  4. Filters the grid via the built lists
  5. Builds the grid
  6. Sets the grid loading to false
  7. Removes any previous filters
  8. Emit ClearFilterText (which clears the filter lists see picture below)

#### Gene Controller
  1. Sets the gene loading to true
  2. Runs the Ajax search method
  3. Runs the filter method 
  4. Removes any filters from the original filter set
  5. Sets the gene loading to false

#### Disease Controller
  1. Sets the disease loading to true
  2. Runs the Ajax search method
  3. Sets the result count based on the data coming back from the server
  4. Runs the filter method
  5. Removed any filters from the original filter set
  6. Sets the disease loading to false
  
### What happens when "Apply Filters" is clicked?

[<img src="www/images/doc/filters.jpg" width="600px">](www/images/doc/filters.jpg)

The image shows what happens with the Gene filter specifically but the same process happens for the disease filter also.

#### Search Controller
  1. vm.applyFilters on the Search Controller is run
  2. Emit FilterChanged Event to the Grid controllers

#### Grid Controller
  1. Catch FilterChanged Event
  2. Sets grid loading to true
  3. Filters the grid based on the current filter set
  4. Emits GridFilterFinished event
  5. Builds the grid based the data that is left from the filter
  6. Sets grid loading to false

#### Disease Controller
  1. Catch GridFilterFinished event
  2. Runs Filter Method with new filter values

#### Gene Controller
  1. Catch GridFilterFinished event
  2. Runs Filter Method with new filter values

## Scope

### Root Scope 
This surrounds the entire page on the html tag in the [header.jsp](www/WEB-INF/jsp/hmdc/header.jsp) page via the ```<html ng-app="HMDCApplication">``` The [app.module.js](www/hmdc/app/app.module.js) defines the application and the modules that its uses.

[<img src="www/images/doc/hmdc_scope.jpg" width="300px">](www/images/doc/hmdc_scope.jpg)

### Search Controller Scope
This is based on the Search Controller which surronds the [searchTemplate.html](www/hmdc/app/components/search/searchTemplate.html). ```<div ng-controller="SearchController">```

### Grid Controller Scope
This is based on the [gridTemplate.html](www/hmdc/app/components/search/gridTemplate.html) which is included from inside the [searchTemplate.html](www/hmdc/app/components/search/searchTemplate.html).

### Gene Controller Scope
This is based on the [geneTemplate.html](www/hmdc/app/components/search/geneTemplate.html) which is included from inside the [searchTemplate.html](www/hmdc/app/components/search/searchTemplate.html).

### Disease Controller Scope
This is based on the [diseaseTemplate.html](www/hmdc/app/components/search/diseaseTemplate.html) which is included from inside the [searchTemplate.html](www/hmdc/app/components/search/searchTemplate.html).

### vm.*
This is used in each of the controllers for their local model to interact with and have access to the data in the html templates and the javascript. Sometimes this can be shared, if a lower (down the tree) scope does not override it, see picture from above.