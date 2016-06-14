angular.module("naturalSortService", [])

// The core natural service
.factory("naturalSortService", ["$locale", function($locale) {
		// the cache prevents re-creating the values every time, at the expense of
		// storing the results forever. Not recommended for highly changing data
		// on long-term applications.
	var natCache = {},
		// amount of extra zeros to padd for sorting
        padding = function(value) {
			return '00000000000000000000'.slice(value.length);
		},
		
		// Calculate the default out-of-order date format (d/m/yyyy vs m/d/yyyy)
        natDateMonthFirst = $locale.DATETIME_FORMATS.shortDate.charAt(0) == 'm';
		// Replaces all suspected dates with a standardized yyyy-m-d, which is fixed below
        fixDates = function(value) {
			// first look for dd?-dd?-dddd, where "-" can be one of "-", "/", or "."
            return value.replace(/(\d\d?)[-\/\.](\d\d?)[-\/\.](\d{4})/, function($0, $m, $d, $y) {
				// temporary holder for swapping below
                var t = $d;
				// if the month is not first, we'll swap month and day...
                if(!natDateMonthFirst) {
                    // ...but only if the day value is under 13.
                    if(Number($d) < 13) {
                        $d = $m;
                        $m = t;
                    }
                } else if(Number($m) > 12) {
					// Otherwise, we might still swap the values if the month value is currently over 12.
                    $d = $m;
                    $m = t;
                }
				// return a standardized format.
                return $y+'-'+$m+'-'+$d;
            });
        },
		
		// Fix numbers to be correctly padded
        fixNumbers = function(value) {
	 		// First, look for anything in the form of d.d or d.d.d...
            return value.replace(/(\d+)((\.\d+)+)?/g, function ($0, integer, decimal, $3) {
				// If there's more than 2 sets of numbers...
                if (decimal !== $3) {
                    // treat as a series of integers, like versioning,
                    // rather than a decimal
                    return $0.replace(/(\d+)/g, function ($d) {
                        return padding($d) + $d
                    });
                } else {
					// add a decimal if necessary to ensure decimal sorting
                    decimal = decimal || ".0";
                    return padding(integer) + integer + decimal + padding(decimal);
                }
            });
        },

		// Finally, this function puts it all together.
        natValue = function (value) {
            if(natCache[value]) {
                return natCache[value];
            }
				// Remove special chars like , and ; and -
				//console.log(value);
				if(value == null) value = '';
				value = value.toString().replace(/[^a-zA-Z0-9 ]/g, ' ').toLowerCase();
				//console.log(value);
				// Turn extra spaces into 1 space
				value = value.replace(/\s+/g, ' ');
				//console.log(value);
            var newValue = fixNumbers(value);
				//console.log(newValue);
            return natCache[value] = newValue;
        };

	// The actual object used by this service
	return {
		naturalValue: natValue,
		naturalSort: function(property) {
			var order = 1;
			if(property[0] === "-") {
				order = -1;
				property = property.substr(1);
			}
			return function(a, b) {
				a = natValue(a[property]);
				b = natValue(b[property]);
				ret = (a < b) ? -1 : ((a > b) ? 1 : 0);
				return ret * order;
			}
		},
		naturalSortFunction: function(a, b) {
			a = natValue(a);
			b = natValue(b);
			return (a < b) ? -1 : ((a > b) ? 1 : 0);
		}
	};
}])

// Attach a function to the rootScope so it can be accessed by "orderBy"
.run(["$rootScope", "naturalSortService", function($rootScope, naturalService) {
	$rootScope.natural = function (field) {
		return function (object) {
			return naturalService.naturalValue(object[field]);
		}
	};
}]);
