var deleteUserId = '';
var userPanel = new Object();

$(document).ready(function() {
	
	  calculateDropdownSize();
	  

	  $(window).resize(function(){ // Adjusting position and size upon resizing window.
		  
		  calculateDropdownSize();
	  });
	  
	  function calculateDropdownSize() {
		  
		  var containerWidth = $('#adv-search').width(); 
		  $('#searchDropdown').width(containerWidth);
		 
		  var extraWidth = $('#searchDropdown').outerWidth();
		  
		  $('#searchDropdown').width(containerWidth - (extraWidth - containerWidth));
	  }
	
	
	$(document).on('click', '#confirmAdd', function (e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data("url");
		var firstName = $(this).closest('.modal-content').find('[name="firstName"]').val();
		var lastName = $(this).closest('.modal-content').find('[name="lastName"]').val();
		var email = $(this).closest('.modal-content').find('[name="email"]').val();
		var address = $(this).closest('.modal-content').find('[name="address"]').val();
		var city = $(this).closest('.modal-content').find('[name="city"]').val();
		var state = $(this).closest('.modal-content').find('[name="state"]').val();
		var zip = $(this).closest('.modal-content').find('[name="zip"]').val();
		var password = $(this).closest('.modal-content').find('[name="password"]').val();
		
		var form = $(this).closest('.modal-content');
		
		var userJSON =  {firstName: firstName, lastName: lastName, email: email, address: address,
				city: city, state: state, zip: zip, password: password};
		
		console.log(userJSON);
		
		$.ajax({
			
	        type: 'POST',
	        url: url, 
	        contentType: 'application/json',
	        data: JSON.stringify(userJSON),
	        dataType: 'json'
		
	    }).done (function(data) {
	    	
	    	console.log(data);
	    	
	    	showSuccessAlert(data.message);
	    
	    	$('#add-user').modal('toggle');
	    	
	    	//display new user
	    	var id = data.id;
	    	
	    	var userHtml = $('.panel').html();
	    	userHtml = '<div class="row">\n<div class="panel panel-default">\n' + userHtml + "\n</div></div>";
	    	
	    	var index = parseInt($('.index:last').text()) + 1;
	    	
	    	$('#accordion').append(userHtml);
	    	
	    	console.log("index: " + index);
	    	$('.panel-title-text:last').prop('href', '#collapse' + (index - 1));
	    	$('.collapse:last').prop('id', 'collapse' + (index - 1));
	    	$('.panel-title-text:last').html("<span class=\"index\">" + 
	    			index + "</span>. " + firstName + " " + lastName);
	    	$('.userID:last').text(id);
	    	
	    	$('.first-name:last').val(firstName);
	    	
	    	$('.last-name:last').val(lastName);
	    	$('.email:last').val(email);
	    	$('.address:last').val(address);
	    	$('.city:last').val(city);
	    	$('.state:last > option').each(function(i, obj) {
	    		if (state == $(obj).val()) {
	    			$(obj).prop("selected", true);
	    		}
	    		else {
	    			$(obj).prop("selected", false);
	    		}
	    	});
	    	$('.zip:last').val(zip);
	    	$('.is-active:last').prop("checked", false);
	    	
	    	
	    	
	    }).fail (function(err) {
			
	    	console.error(err);
			processErrors(err, form);
			
	    });
	});
	
	$(document).on('click', '#editUser', function (e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data("url");
		var id = $(this).closest('.edit-form').find('.userID').text();
		var firstName = $(this).closest('.edit-form').find('[name="firstName"]').val();
		var lastName = $(this).closest('.edit-form').find('[name="lastName"]').val();
		var email = $(this).closest('.edit-form').find('[name="email"]').val();
		var address = $(this).closest('.edit-form').find('[name="address"]').val();
		var city = $(this).closest('.edit-form').find('[name="city"]').val();
		var state = $(this).closest('.edit-form').find('[name="state"]').val();
		var zip = $(this).closest('.edit-form').find('[name="zip"]').val();
		var password = $(this).closest('.edit-form').find('[name="password"]').val();
		
		var form = $(this).closest('.edit-form');
		
		var active = false;
		if ($(this).closest('.edit-form').find('[name="is-active"]').is(':checked')) {
			console.log('checked');
			active = true;
		}
		
		var userJSON =  {id: id, firstName: firstName, lastName: lastName, email: email, address: address,
				city: city, state: state, zip: zip, password: password, active: active};
		
		console.log(userJSON);
		
		$.ajax({
			
	        type: 'POST',
	        url: url, 
	        contentType: 'application/json',
	        data: JSON.stringify(userJSON),
	        dataType: 'json'
		
	    }).done (function(data) {
	    	
	    	console.log(data);

	    	showSuccessAlert(data.message);
	    
	    	$("html, body").animate({ scrollTop: 0 }, "slow");
	    	
	    }).fail (function(err) {
			
	    	console.error(err);
			processErrors(err, form);
			
	    });
	});
	
	$('#deleteUser').on('show.bs.modal', function (e) {
	    var trigger = $(e.relatedTarget);
	    var id = $(trigger).closest('.edit-form').find('.userID').text();
	    
	    deleteUserId = id;
	    userPanel = $(trigger).closest('.panel');
	});
	
	$(document).on('click', '#confirmDeleteUser', function (e) {
		
		e.preventDefault();
		
		var url = $(this).data("url");
		
		var id = deleteUserId;
		var panel = userPanel;
		
		console.log("url: " + url + ", id: " + id);
		
		$.ajax({
			
	        type: 'POST',
	        url: url,
	        data: {"id": id},
	        dataType: 'json'
		
	    }).done (function(data) {
	    	
	    	console.log(data);
	    	
	    	showSuccessAlert(data.message);
	    	
	    	$("html, body").animate({ scrollTop: 0 }, "slow");
	    	
	    	$(panel).remove();
	    	
	    	$('#deleteUser').modal('toggle');
	    
	    }).fail (function(err) {
			
	    	console.error(err);
			
	    });
		
	});
	
	$(document).on('click', '.pager_item', function (e) {
		e.preventDefault();
		var url = $(this).data("page");
		
		var res = url.split("=");
		var urlBase = res[0] + "=";
		var page = res[1];
		
		console.log(url);
		
		$.ajax({
	        url:url,
	        type: "GET",
	        dataType: 'json'
	        	
		}).done (function(data) {
			
			console.log(data);
			
			//update user information
			updateUsersList(data.object);
			
			//update pagination
			var numberOfPages = data.totalPageCount;
			updatePagination(urlBase, page, numberOfPages);
			
			
		}).fail (function(err) {
		    console.error(err);
		       
		});
		
	});
	
	$(document).on('click', '#searchUser', function(e) {
		
		e.preventDefault();
		
		var url = $(this).data('url');
		var query = $('#searchInput').val();
		
		console.log("url: " + url + ", query: " + query);
		
		$.ajax({
		
			url: url,
			type: "POST",
			data: {"query": query},
			dataType: 'json'
				
		}).done (function(data) {
			
			
			//add search results menu
			if(data.object.length == 0) {
				$('.search-result-text').text("No results found"); 
			}
			else {
				$('.search-result-text').text("Search Results:"); 
			}
			$('.search-results').removeClass('hidden');
			
			//update user information
			updateUsersList(data.object);
			
			
			//update pagination to the 1 page
			var page = data.currentPage;
			var numberOfPages = data.totalPageCount;
			var pageUrl = $('#pageUrl').val();
			var res = pageUrl.split("=");
			var urlBase = res[0] + "=";
			
			$('#pageUrl').val(urlBase + page);
			updatePagination(urlBase, page, numberOfPages);
			
				
			
		}).fail (function(err) {
			console.error(err);
		});
		
		
	});
	
	function updateUsersList(data) {
		
		//show users for the selected page
		$('.panel').each(function(i, obj) {
			
			var user = data[i];
			if (user == null) {
				$(obj).hide();
			}
			else {
				$(obj).show();
				
				var firstName = user.firstName;
				var lastName = user.lastName;
				var id = user.id;
				var email = user.email;
				var address = user.address;
				var city = user.city;
				var state = user.state;
				var zip = user.zip;
				var active = user.active;
				
				$(obj).find('.name').text(firstName + ' ' + lastName);
				$(obj).find('.userID').text(id);
				$(obj).find('.first-name').val(firstName);
				$(obj).find('.last-name').val(lastName);
				$(obj).find('.email').val(email);
				$(obj).find('.address').val(address);
				$(obj).find('.city').val(city);
				
				$(obj).find('.state > option').each(function(j, optObj) {
		    		if (state == $(optObj).val()) {
		    			$(optObj).prop("selected", true);
		    		}
		    		else {
		    			$(optObj).prop("selected", false);
		    		}
		    	});
				
				$(obj).find('.zip').val(zip);
				if (active) {
					$(obj).find('.is-active').prop("checked", true);
				}
				else {
					$(obj).find('.is-active').prop("checked", false);
				}
			}
	    	
		});
		
		
	}
	
	$(document).on('click', '#view-all', function (e) {
		
		e.preventDefault();
		
		$('.search-results').addClass('hidden');
		
		var url = $(this).data("url");
		
		
		console.log(url);
		
		$.ajax({
	        url:url,
	        type: "GET",
	        dataType: 'json'
	        	
		}).done (function(data) {
			
			console.log(data);
			
			//update user information
			updateUsersList(data.object);
			
			//update pagination
			
			var page = data.currentPage;
			var numberOfPages = data.totalPageCount;
			var pageUrl = $('#pageUrl').val();
			var res = pageUrl.split("=");
			var urlBase = res[0] + "=";
			
			$('#pageUrl').val(urlBase + page);
			
			updatePagination(urlBase, page, numberOfPages);
			
			
		}).fail (function(err) {
		    console.error(err);
		       
		});
		
	});
	
	$(document).on('click', '#preciseSearch', function(e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data('url');
		
		var id = $('#adv-search').find('.user-id').val();
		
		var active = $('#adv-search').find('.state-filter').val();
		var firstName = $('#adv-search').find('.first-name').val();
		var lastName = $('#adv-search').find('.last-name').val();
		var email = $('#adv-search').find('.email').val();
		var address = $('#adv-search').find('.address').val();
		var city = $('#adv-search').find('.city').val();
		var state = $('#adv-search').find('.state').val();
		var zip = $('#adv-search').find('.zip').val();
		
		var searchJSON = {id: id, firstName: firstName, lastName: lastName, email: email, address: address,
				city: city, state: state, zip: zip, active: active};
			
		var form = $(this).closest('.search-form');
		
		$.ajax({
		
			url: url,
			type: "POST",
			contentType: 'application/json',
	        data: JSON.stringify(searchJSON),
			dataType: 'json'
				
		}).done (function(data) {
			
			
			//add search results menu
			if(data.object.length == 0) {
				$('.search-result-text').text("No results found"); 
			}
			else {
				$('.search-result-text').text("Search Results:"); 
			}
			$('.search-results').removeClass('hidden');
			
			//update user information
			updateUsersList(data.object);
			
			
			//update pagination to the 1 page
			var page = data.currentPage;
			var numberOfPages = data.totalPageCount;
			var pageUrl = $('#pageUrl').val();
			var res = pageUrl.split("=");
			var urlBase = res[0] + "=";
			
			$('#pageUrl').val(urlBase + page);
			updatePagination(urlBase, page, numberOfPages);
			
			$("#searchDropdown").slideToggle();
			
			
		}).fail (function(err) {
			console.error(err);
			processErrors(err, form);
			
		});
		
		
	});
	
	$(document).on('click', '#openSearch', function(e) {
		
		$("#searchDropdown").slideToggle();
		
	});
	
	$(document).on('click', '#clearForm', function (e) {
		
		e.preventDefault();
		
		$(this).closest('form').find("input").val("");
		
	});
	
	
});