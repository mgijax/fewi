# HMDC Documentation

## JSP
### [home.jsp](www/WEB-INF/jsp/hmdc/home.jsp)
  This is the main file that is used for the HMDC home page. This page includes all the standard header and footer. 

## Controllers
### [DiseaseController](www/hmdc/app/components/search/DiseaseController.js)
This controller handles the filtering for the disease table, as well as using the Search Service to do the data query for the table. This controller also handles the popup for the "Source" link on the smart table.
### [GeneController](www/hmdc/app/components/search/GeneController.js)
This controller handles the filtering for the disease table, as well as using the Search Service to do the data query for the table. This controller also handles the popup for the "Source" link on the smart table.
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
### [OrderHashByFilter]( www/hmdc/app/components/search/OrderHashByFilter.js)
This filter is used on different columns in the smart tables to orber by that field of data by chaining it by a |
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
  7. Removes any pervious filters
  8. Emit ClearFilterText

#### Gene Controller
  1. Sets the gene loading to true
  2. Runs the Ajax search method
  3. Runs the filter method 
  4. Removes any filters from the original filter set
  5. Sets the gene loading to false

#### Disease Controller
  1. Sets the disease loading to true
  2. Runs the Ajax search method
  3. Sets the result count based on the data coming back form the server
  4. Runs the filter method
  5. Removed any filters from the original filter set
  6. Sets the disease loading to false
  
### What happens when "Apply Filters" is clicked?

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
This surrounds the entire page on the html tag in the [header.jsp](www/WEB-INF/jsp/hmdc/header.jsp) page via the ```<html ng-app="HMDCApplication">```

### Search Controller Scope
This is based on the Search Controller which surronds the [searchTemplate.html](www/hmdc/app/components/search/searchTemplate.html). ```<div ng-controller="SearchController">```

### Grid Controller Scope
This is based on the [gridTemplate.html](www/hmdc/app/components/search/gridTemplate.html) which is included from inside the [searchTemplate.html](www/hmdc/app/components/search/searchTemplate.html).

### Gene Controller Scope
This is based on the [geneTemplate.html](www/hmdc/app/components/search/geneTemplate.html) which is included from inside the [searchTemplate.html](www/hmdc/app/components/search/searchTemplate.html).

### Disease Controller Scope
This is based on the [diseaseTemplate.html](www/hmdc/app/components/search/diseaseTemplate.html) which is included from inside the [searchTemplate.html](www/hmdc/app/components/search/searchTemplate.html).

