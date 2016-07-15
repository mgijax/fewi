# HMDC Documentation

## JSP
### www/WEB-INF/jsp/hmdc/home.jsp:
  This is the main file that is used for the HMDC home page. This page includes all the standard header and footer. 

## Controllers
### www/hmdc/app/components/search/DiseaseController.js
This controller handles the filtering for the disease table, as well as using the Search Service to do the data query for the table. This controller also handles the popup for the "Source" link on the smart table.
### www/hmdc/app/components/search/GeneController.js
This controller handles the filtering for the disease table, as well as using the Search Service to do the data query for the table. This controller also handles the popup for the "Source" link on the smart table.
### www/hmdc/app/components/search/GridController.js
This controller handles the following:

  1. Popups
  2. Custom methods for the grid formating on the ng-cells directive
  3. Grid filtering
  4. Gird Search
  5. Updates the amount of rows and columns to show based on window size
  6. Builds out the filter lists for all the tabs
  7. Filters / Builds the grid.
  8. Changes the JSON String coming from Fewi into the format needed for the ng-cells grid

### www/hmdc/app/components/search/SearchController.js
This controller handles the following:

  1. Setting up the filter settings options
  2. The cooridnation of events between the tabs for the filters
  3. The "You Searched For" text
  4. The click on the search buttton from the main query form
  5. Parses the gene upload file.
  6. Definition of the tabs
  7. Definition of the query form

## Services
### www/hmdc/app/components/search/AutoCompleteService.js
This service provides a method for getting the REST endpoint for the autocomplete controller, for HMDC. This method takes the data in the query form in order to process the search to solr.
### www/hmdc/app/components/search/SearchService.js
This service provides three methods used for doing Ajax calls to the server for each of the three tabs. All of them take the formData from the query form and pass that data to the server for processing.
### www/hmdc/app/components/search/NaturalSortService.js
This service provides a method that can be used to "natural sort" the list that is passed to it. This might be better known as Smart Alpha sorting.

## Directives
### www/hmdc/app/components/search/SearchStResetDirective.js
This directive is used to add to the different tables in order to get the "reset" functionality for when the table data changes.

## Filters
### www/hmdc/app/components/search/OrderHashByFilter.js
This filter is used on different columns in the smart tables to orber by that field of data by chaining it by a |
### www/hmdc/app/components/search/SearchHandleSubscriptFilter.js
This filter is used to handle sub scripting on different fields, by adding to the field via a |
### www/hmdc/app/components/search/SearchSortFilter.js
This filter is used to sort the columns of the smart tables by the header of the column. When clicking on the column heading the sort will reverse


## Templates - HTML
### www/hmdc/app/components/search/searchTemplate.html
This file is used to display the search query form, and all the tabs that are driven by the model in the search controller.
### www/hmdc/app/components/search/diseaseTemplate.html
This file is used to display the disease table. It makes use of angular Smart Table
### www/hmdc/app/components/search/geneTemplate.html
This file is used to display the gene table. It makes use of angular Smart Table
### www/hmdc/app/components/search/gridTemplate.html
This file is used to display the HMDC grid. It makes use of angular ng-cells module.




