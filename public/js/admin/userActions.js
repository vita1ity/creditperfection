var deleteUserId = '';
var userPanel = new Object();

$(document).ready(function() {
		
	  calculateDropdownSize();

	  $(window).resize(function(){
		  
		  calculateDropdownSize();
	  });
	  
	  function calculateDropdownSize() {
		  
		  var containerWidth = $('#adv-search').prop("clientWidth"); 
		  
		  var contWidth = $('#adv-search').width();
		  
		  $('#searchDropdown').width(containerWidth);
		 
		  var extraWidth = $('#searchDropdown').outerWidth();
		  
		  $('#searchDropdown').width(containerWidth - (extraWidth - containerWidth));
		  
		  
	  }
	
	$('#add-user').keypress(function (e) {
		  if (e.which == 13) {
		    $('#confirmAdd').click();
		    return false;  
		  }
	});
	
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
	    	
	    	var visibleItems = 0;
	    	$('.panel:visible').each(function(i, obj) {
	    		visibleItems++;
	    	});
	    	
	    	if (visibleItems != 10) {
	    	
		    	var index = ++visibleItems;
		    	
		    	$('.panel').not(':visible').each(function(i, obj) {
		    		
		    		if (i == 1) {
		    			
		    			$(obj).show();
		    	
				    	$(obj).find('.panel-title-text').html("<span class=\"index\">" + 
				    			index + "</span>. " + firstName + " " + lastName);
				    	$(obj).find('.userID').text(id);
				    	
				    	$(obj).find('.first-name').val(firstName);
				    	
				    	$(obj).find('.last-name').val(lastName);
				    	$(obj).find('.email').val(email);
				    	$(obj).find('.address').val(address);
				    	$(obj).find('.city').val(city);
				    	$(obj).find('.state > option').each(function(j, obj2) {
				    		if (state == $(obj2).val()) {
				    			$(obj2).prop("selected", true);
				    		}
				    		else {
				    			$(obj2).prop("selected", false);
				    		}
				    	});
				    	$(obj).find('.zip').val(zip);
				    	$(obj).find('.is-active').prop("checked", false);
		    		}
		    	});
	    	}
	    	
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
	    	
	    	$(panel).hide();
	    	
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
			
			if (data.object != null) {
				//update user information
				updateUsersList(data.object);
			}
			//search results active
			else {
				updateUsersList(data.users);
				showSubscriptions(data.subscriptions);
			}
			
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
			if(data.users.length == 0) {
				$('.search-result-text').text("No results found"); 
			}
			else {
				$('.search-result-text').text("Search Results:"); 
			}
			$('.search-results').removeClass('hidden');
			
			//update user information
			updateUsersList(data.users);
			showSubscriptions(data.subscriptions);
			
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
				
				$(obj).find('.subscription-form').addClass('hidden');
				
			}
	    	
		});
		
		
	}
	
	function showSubscriptions(data) {
		$('.panel').each(function(i, obj) {
			
			var subscription = data[i];
			
			if (subscription != null) {
				
				var subscriptionForm = $(obj).find('.subscription-form');
				$(subscriptionForm).removeClass('hidden');
				
				
				var id = subscription.id;
				var status = subscription.status;
				var subscriptionDate = subscription.subscriptionDate;
				var lastPaymentDate = subscription.lastChargeDate;
				
				//user properties
				var userId = subscription.user.id;
				var firstName = subscription.user.firstName;
				var lastName = subscription.user.lastName;
				var userId = subscription.user.id;
				var email = subscription.user.email;
				
				//credit card properties
				var cardId = subscription.creditCard.id;
				var cardName = subscription.creditCard.name;
				var cardType = subscription.creditCard.cardType;
				var cardNumber = subscription.creditCard.digits;
				var expDate = subscription.creditCard.expDate;
				var cvv = subscription.creditCard.cvv;
				
				//product properties
				var productId = subscription.product.id;
				var productName = subscription.product.name;
				var productPrice = subscription.product.price;
				var productSalePrice = subscription.product.salePrice;
				
				
				$(subscriptionForm).find('.subscriptionID').text(id);
				
				$(obj).find('.user-id').text(userId);
				$(obj).find('.first-name').text(firstName);
				$(obj).find('.last-name').text(lastName);
				$(obj).find('.email').text(email);
				
				$(subscriptionForm).find('.card-id').text(cardId);
				$(subscriptionForm).find('.card-name').text(cardName);
				$(subscriptionForm).find('.card-type').text(cardType);
				$(subscriptionForm).find('.card-number').text(cardNumber);
				$(subscriptionForm).find('.exp-date').text(expDate);
				$(subscriptionForm).find('.cvv').text(cvv);

				$(subscriptionForm).find('.product-id').text(productId);
				$(subscriptionForm).find('.product-name').text(productName);
				$(subscriptionForm).find('.product-price').text(productPrice);
				$(subscriptionForm).find('.sale-price').text(productSalePrice);
				
				$(subscriptionForm).find('.status').text(status);
				$(subscriptionForm).find('.subscription-date').text(subscriptionDate);
				$(subscriptionForm).find('.last-payment-date').text(lastPaymentDate);
				
				if (status == 'PENDING') {
					$(subscriptionForm).find('.cancel-subscription').removeClass('hidden');
				}
				else {
					$(subscriptionForm).find('.cancel-subscription').addClass('hidden');
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
			if(data.users.length == 0) {
				$('.search-result-text').text("No results found"); 
			}
			else {
				$('.search-result-text').text("Search Results:"); 
			}
			$('.search-results').removeClass('hidden');
			
			//update user information
			updateUsersList(data.users);
			showSubscriptions(data.subscriptions);
			
			
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
		e.stopPropagation(); 
		
	});
	
	$(document).on('click', '#clearForm', function (e) {
		
		e.preventDefault();
		
		$(this).closest('form').find("input").val("");
		
	});
	
	var $userAccordion = $('#userAccordion');
	$userAccordion.on('show.bs.collapse','.collapse', function() {
		
	    $userAccordion.find('.collapse.in').collapse('hide');
	});
	
	$(document).click(function(){
		$("#searchDropdown").slideUp();
	});

	
	
	
});