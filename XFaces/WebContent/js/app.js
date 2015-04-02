/*jslint browser: true, devel: true, white: true, plusplus: true */
/*global devel, jQuery, $, HTMLElement */

$(function(){
   "use strict";

   function showModalProfile(dataRow) {
      var template = document.querySelector('#modal-profile'),
          clone = document.importNode(template.content, true),
          modal;
      $('body').remove('#mod');
      clone.querySelector('.name').innerHTML = dataRow.default;
      clone.querySelector('.position').innerHTML = dataRow.title;
      clone.querySelector('.region').innerHTML = dataRow.region;
      clone.querySelector('.phone').innerHTML = dataRow.phone;
      clone.querySelector('.email').innerHTML = dataRow.email;
      clone.querySelector('.email').setAttribute('href', 'mailto:' + dataRow.email);

      $('body').prepend(clone);
      modal = $('#mod');
      modal.modal();
   }



   function cloneElement(template, dataRow) {
      var emailAttr,
          clone;

      clone = document.importNode(template, true);
      // Cache-busting!
      clone.querySelector('.profile-image').src = "http://lorempixel.com/600/600/?q=" + Math.random();
      clone.querySelector('.name').innerHTML = dataRow.default;
      clone.querySelector('.position').innerHTML = dataRow.title
      emailAttr = clone.querySelector('.email');
      emailAttr.innerHTML = dataRow.email;
//      emailAttr.setAttribute('href', 'mailto:' + dataRow.email);

      return $(clone);
   }

   function renderResults(data) {
      var profile,
          clone,
          row = $('#content-row'),
          profile_type = 'large-profile',
          dataRows = data.rows,
          i;
      
      row.empty();
      
      if(data.total_rows == 0) {
         var alertDiv = document.querySelector('#no-results');
         alertDiv.innerHTML = "No results found. You can try searches like:" + 
            "<ul><li>Jason G</li>" + 
            "<li>\"Jason Killian\"</li>" +
            "<li>EID:E123456</li>" +
            "<li>email:lmsurpre@us.ibm.com</li>" +
            "<li>Lee AND email:lmsurpre</li></ul>";
         $(alertDiv).fadeIn(300);
      }
      else if(data.total_rows > dataRows.length) {
         var alertDiv = document.querySelector('#more-results');
         alertDiv.innerHTML = "Showing " + dataRows.length + " of " + data.total_rows + " results. Try narrowing your search.  For example:"+
	      	"<ul><li>\"Jason Killian\"</li>" +
	     	"<li>EID:E123456</li>" +
	     	"<li>email:lmsurpre@us.ibm.com</li>" +
	     	"<li>Lee AND email:lmsurpre</li></ul>";
         $(alertDiv).delay(1000).fadeIn(300);
      }

      if(dataRows.length > 6) {
         profile_type = 'small-profile';
      }

      profile = document.querySelector('#' + profile_type);
      
      for(i = 0; i < dataRows.length; ++i) {
         clone = cloneElement(profile.content, dataRows[i].fields).children()[0];
         row.append(clone).hide().fadeIn('300');
      }

      $('.profile').on('click', function(e) {
         var index = $('.profile').index(this);
         showModalProfile(dataRows[index].fields);
      });
   }

   function search() {
      var text = $('#nameInput').val();     
      $('.alert').hide();
      $.get('/faces',{q: text}, function (data, status, xhr) {
         renderResults(data);
      }, 'json').fail(function() { 
         var alertDiv = document.querySelector('#no-results');
         alertDiv.innerHTML = "Error connecting to the database."
         $(alertDiv).fadeIn(300);
      });
   }
   
   //start-up code
   $('.alert').hide();

   $('#nameInput').on('keypress', function (e) {
      if (e.which == 13) {
         search();
         e.preventDefault();
      }
   });

   $('#searchBtn').on('click', function() {
      search();
   });

});