(function() {
  'use strict';
  angular.module('hmdc.config')
    .constant('formConfig', {
      options: {
        labelColWidth: 3,
        inputColWidth: 6,
        helpColWidth: 5
      }
    })
    .run(formlyTemplatesRun);

  // @ngInject
  function formlyTemplatesRun(formlyValidationMessages) {
    // default messages
    formlyValidationMessages.addTemplateOptionValueMessage('minlength', 'minlength', '', 'is the minimum length', 'Too short.');
    formlyValidationMessages.addTemplateOptionValueMessage('maxlength', 'maxlength', '', 'is the maximum length', 'Too long.');
    formlyValidationMessages.addStringMessage('required', 'This field is required.');
    formlyValidationMessages.addStringMessage('notfound', 'Valid entity could not be found.');
  }
})();
