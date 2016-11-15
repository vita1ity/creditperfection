var deleteSubscriptionId = '';
var subscriptionPanel = new Object();

$(document).ready(function() {
	
$(document).on('click', '#confirmAddSubscription', function(e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data('url');
		var userId = $(this).closest('.modal-content').find('[name="user"]').val();
		var cardId = $(this).closest('.modal-content').find('[name="creditCard"]').val();
		var productId = $(this).closest('.modal-content').find('[name="product"]').val();
		
		var form = $(this).closest('.modal-content');
		
		var subscriptionJSON = {userId: userId, cardId: cardId, productId: productId};
		
		console.log(subscriptionJSON);
		
		$.ajax({
			
			type: 'POST',
			url: url,
			contentType: 'application/json',
			data: JSON.stringify(subscriptionJSON),
			dataType: 'json'
			
		}).done (function(data) {
			
			console.log(data);

			showSuccessAlert(data.message);
	    
	    	$('#add-subscription').modal('toggle');
			
	    	//display new subscription
	    	var id = data.object.id;
	    	
	    	var userId = data.object.user.id;
	    	var firstName = data.object.user.firstName;
	    	var lastName = data.object.user.lastName;
	    	var email = data.object.user.email;
	    	
	    	var cardId = data.object.creditCard.id;
	    	var cardName = data.object.creditCard.name;
	    	var cardType = data.object.creditCard.cardType;
	    	var digits = data.object.creditCard.digits;
	    	var expDate = data.object.creditCard.expDate;
	    	var cvv = data.object.creditCard.cvv;
	    	
	    	var productId = data.object.product.id;
	    	var productName = data.object.product.name;
	    	var productPrice = data.object.product.price;
	    	var productSalePrice = data.object.product.salePrice;
	    	
	    	var status = data.object.status;
	    	var subscriptionDate = data.object.subscriptionDate;
	    	
	    	var subscriptionHtml = $('.panel').html();
	    	subscriptionHtml = '<div class="panel panel-default">\n' + subscriptionHtml + "\n</div>";
	    	
	    	var index = parseInt($('.index:last').text()) + 1;
	    	
	    	$('#accordion').append(subscriptionHtml);
	    	
	    	console.log("index: " + index);
	    	
	    	$('.panel-title-text:last').prop('href', '#collapse' + (index - 1));
	    	$('.collapse:last').prop('id', 'collapse' + (index - 1));
	    	$('.panel-title-text:last').html("<span class=\"index\">" + 
	    			index + "</span>. " + firstName + " " + lastName + " - " + productName);
	    	$('.subscriptionID:last').text(id);
	    	
	    	$('.user-id:last').text(userId);
	    	$('.first-name:last').text(firstName);
	    	$('.last-name:last').text(lastName);
	    	$('.email:last').text(email);
			
	    	$('.card-id:last').text(cardId);
	    	$('.card-name:last').text(cardName);
	    	$('.card-type:last').text(cardType);
	    	$('.card-number:last').text(digits);
	    	$('.exp-date:last').text(expDate);
	    	$('.cvv:last').text(cvv);
	    	
	    	$('.product-id:last').text(productId);
	    	$('.product-name:last').text(productName);
	    	$('.product-price:last').text(productPrice);
	    	$('.sale-price:last').text(productSalePrice);
	    	
	    	$('.status:last').text(status);
	    	var dateStr = subscriptionDate[0] + "-" + subscriptionDate[1] + "-" + subscriptionDate[2] + "T" + 
	    		subscriptionDate[3] + ":" + subscriptionDate[4] + ":" + subscriptionDate[5]; 
	    	$('.subscription-date:last').text(dateStr);
	    	
		}).fail (function(err) {
			
			console.log(err);
			processErrors(err, form);
		})
		
	});
	
	$(document).on('click', '#editSubscription', function(e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var subscriptionId = $(this).closest('.edit-form').find('.subscriptionID').text();
		var userId = $(this).closest('.edit-form').find('.user-id').text();
		var cardId = $(this).closest('.edit-form').find('.card-id').text();
		var productId = $(this).closest('.edit-form').find('.product-id').text();
		var status = $(this).closest('.edit-form').find('.status').text();
		
		subscriptionPanel = $(this).closest('.edit-form');
		
		console.log(userId);
		
		$('#subscriptionIdModal').val(subscriptionId);
		
		$('#userSubscriptionEdit > option').each(function() {
			
			if (this.value == userId) {
				$(this).attr("selected", "selected");
			}
			
		});
		
		$('#productSubscriptionEdit > option').each(function() {
			
			if (this.value == productId) {
				$(this).attr("selected", "selected");
			}
			
		});
		
		
		$('#statusSubscriptionEdit > option').each(function() {
			
			if (this.value == status) {
				$(this).attr("selected", "selected");
			}
			
		});
		
		var url = $('#user').data('url');
		
		$.ajax({
			
			type: 'GET',
	        url: url,
	        data: {'userId': userId},
	        dataType: 'json'
			
		}).done (function(data) {
			
			console.log(data);
			
			var optionsHtml ='';
			
			for (var i = 0; i < data.length; i++) {
				var creditCard = data[i];
				if (creditCard.id == cardId) {
					optionsHtml += "<option selected value=\"" + creditCard.id + "\"> " + creditCard.id + ". " + creditCard.cardType + 
					": " + creditCard.digits + " </option> \n";
				}
				else {
					optionsHtml += "<option value=\"" + creditCard.id + "\"> " + creditCard.id + ". " + creditCard.cardType + 
						": " + creditCard.digits + " </option> \n";
				}
			}
			
			$('#creditCardSubscriptionEdit').html(optionsHtml);
			$('#creditCardSubscriptionEdit').prop("disabled", false);

		}).fail (function(err) {
			
			console.error(err);
			
		});
		
		$('#edit-subscription').modal('toggle');
		
	});
	
	$(document).on('click', '#confirmEditSubscription', function(e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data('url');
		
		var id = $('#subscriptionIdModal').val();
		var userId = $('#userSubscriptionEdit').val();
		var cardId = $('#creditCardSubscriptionEdit').val();
		var productId = $('#productSubscriptionEdit').val();
		var status = $('#statusSubscriptionEdit').val();
		
		var form = $(this).closest('.modal-content');
		
		var subscriptionJSON = {id: id, userId: userId, cardId: cardId, productId: productId, status: status};
		
		console.log(subscriptionJSON);
		
		$.ajax({
			
			type: 'POST',
			url: url,
			contentType: 'application/json',
			data: JSON.stringify(subscriptionJSON),
			dataType: 'json'
			
		}).done (function(data) {
			
			console.log(data);
			
			showSuccessAlert(data.message);
	    
	    	$('#edit-subscription').modal('toggle');
	    	$("html, body").animate({ scrollTop: 0 }, "slow");
	    	
	    	//display edited subscription
	    	var userId = data.object.user.id;
	    	var firstName = data.object.user.firstName;
	    	var lastName = data.object.user.lastName;
	    	var email = data.object.user.email;
	    	
	    	var cardId = data.object.creditCard.id;
	    	var cardName = data.object.creditCard.name;
	    	var cardType = data.object.creditCard.cardType;
	    	var digits = data.object.creditCard.digits;
	    	var expDate = data.object.creditCard.expDate;
	    	var cvv = data.object.creditCard.cvv;
	    	
	    	var productId = data.object.product.id;
	    	var productName = data.object.product.name;
	    	var productPrice = data.object.product.price;
	    	var productSalePrice = data.object.product.salePrice;
	    	
	    	var status = data.object.status;
	    	
	    	var index = parseInt($('.index:last').text());
	    	$('.panel-title-text:last').html("<span class=\"index\">" + 
	    			index + "</span>. " + firstName + " " + lastName + " - " + productName);
	    	
	    	$(subscriptionPanel).find('.user-id').text(userId);
	    	$(subscriptionPanel).find('.first-name').text(firstName);
	    	$(subscriptionPanel).find('.last-name').text(lastName);
	    	$(subscriptionPanel).find('.email').text(email);
	    	
	    	$(subscriptionPanel).find('.card-id').text(cardId);
	    	$(subscriptionPanel).find('.card-name').text(cardName);
	    	$(subscriptionPanel).find('.card-type').text(cardType);
	    	$(subscriptionPanel).find('.card-number').text(digits);
	    	$(subscriptionPanel).find('.exp-date').text(expDate);
	    	$(subscriptionPanel).find('.cvv').text(cvv);
	    	
	    	$(subscriptionPanel).find('.product-id').text(productId);
	    	$(subscriptionPanel).find('.product-name').text(productName);
	    	$(subscriptionPanel).find('.product-price').text(productPrice);
	    	$(subscriptionPanel).find('.sale-price').text(productSalePrice);
	    	
	    	$(subscriptionPanel).find('.status').text(status);
	    	
	    	if (status == 'PENDING') {
		    	$(subscriptionPanel).find('.cancel-subscription').removeClass('hidden');
	    		
	    	}
	    	else {
	    		$(subscriptionPanel).find('.cancel-subscription').addClass('hidden');
	    	}
			
		}).fail (function(err) {
			
			console.log(err);
			processErrors(err, form);
		})
		
	});
	
	$('#deleteSubscription').on('show.bs.modal', function (e) {
	    var trigger = $(e.relatedTarget);
	    var id = $(trigger).closest('.edit-form').find('.subscriptionID').text();
	    
	    deleteSubscriptionId = id;
	    subscriptionPanel = $(trigger).closest('.panel');
	});
	
	$(document).on('click', '#confirmDeleteSubscription', function(e) {
		
		e.preventDefault();
		
		var url = $(this).data('url');
		var id = deleteSubscriptionId;
		var panel = subscriptionPanel; 
		
		$.ajax({
			
			type: 'POST',
			url: url,
			data: {id: id},
			dataType: 'json'
			
		}).done (function(data) {
			
			console.log(data);
			
			showSuccessAlert(data.message);
	    
	    	$(panel).remove();
	    	
	    	$('#deleteSubscription').modal('toggle');
	    	
	    	$("html, body").animate({ scrollTop: 0 }, "slow");
			
		}).fail (function(err) {
			
			console.error(err);
			
		});
		
	});
	
	$('#cancelSubscriptionAdmin').on('show.bs.modal', function (e) {
	    var trigger = $(e.relatedTarget);
	    var id = $(trigger).closest('.edit-form').find('.subscriptionID').text();
	    
	    deleteSubscriptionId = id;
	    subscriptionPanel = $(trigger).closest('.panel');
	});
	
	$(document).on('click', '#confirmCancelSubscriptionAdmin', function(e) {
		
		e.preventDefault();
		
		var url = $(this).data('url');
		var id = deleteSubscriptionId;
		var panel = subscriptionPanel; 
		
		$.ajax({
			
			type: 'POST',
			url: url,
			data: {id: id},
			dataType: 'json'
			
		}).done (function(data) {
			
			console.log(data);
			
			showSuccessAlert(data.message);
	    
			$(panel).find('.status').text('CANCELLED');
			$(panel).find('.cancel-btn').addClass('hidden');
	    	
	    	$('#cancelSubscriptionAdmin').modal('toggle');
	    	
	    	$("html, body").animate({ scrollTop: 0 }, "slow");
			
		}).fail (function(err) {
			
			console.error(err);
			
		});
		
	});
	
	$(document).on('change', '#subscriptionFilterSelect', function (e) {
		
		e.preventDefault();
		
		var url = $(this).data('url');
		var status = $(this).find(":selected").val();
		
		$.ajax({
			
			type: 'POST',
			url: url,
			data: {status: status},
			dataType: 'json'
				
		}).done (function(data){
			
			console.log(data);
			
			var subscriptions = data.object;
			
			var totalPageCount = data.totalPageCount;
			console.log(subscriptions);
			console.log("totalPageCount: " + totalPageCount);
			
			
			var subscriptionsHtml = $('.panel').html();
			subscriptionsHtml = '<div class="panel panel-default hidden">\n' + subscriptionsHtml + "\n</div>";
			
			$('#accordion').html(subscriptionsHtml);
			
			for (var i = 0; i < subscriptions.length; i++) {
				
				var subscription = subscriptions[i];
				
		    	var id = subscription.id;
		    	
		    	var userId = subscription.user.id;
		    	var firstName = subscription.user.firstName;
		    	var lastName = subscription.user.lastName;
		    	var email = subscription.user.email;
		    	
		    	var productId = subscription.product.id;
		    	var productName = subscription.product.name;
		    	var productPrice = subscription.product.price;
		    	var productSalePrice = subscription.product.salePrice;
		    	
		    	var status = subscription.status;
		    	var subscriptionDate = subscription.subscriptionDate;
		    	
		    	var subscriptionHtml = $('.panel').html();
		    	subscriptionHtml = '<div class="panel panel-default">\n' + subscriptionHtml + "\n</div>";
		    	
		    	var index = parseInt($('.index:last').text()) + 1;
		    	
		    	$('#accordion').append(subscriptionHtml);
		    	
		    	console.log("index: " + index);
		    	
		    	$('.panel-title-text:last').prop('href', '#collapse' + (index - 1));
		    	$('.collapse:last').prop('id', 'collapse' + (index - 1));
		    	$('.panel-title-text:last').html("<span class=\"index\">" + 
		    			index + "</span>. <span class=\"name\">" + firstName + " " + lastName + " - " + productName + "</span>");
		    	$('.subscriptionID:last').text(id);
		    	
		    	$('.user-id:last').text(userId);
		    	$('.first-name:last').text(firstName);
		    	$('.last-name:last').text(lastName);
		    	$('.email:last').text(email);
				
		    	$('.product-id:last').text(productId);
		    	$('.product-name:last').text(productName);
		    	$('.product-price:last').text(productPrice);
		    	$('.sale-price:last').text(productSalePrice);
		    	
		    	$('.status:last').text(status);
		    	var dateStr = subscriptionDate[0] + "-" + subscriptionDate[1] + "-" + subscriptionDate[2] + "T" + 
		    		subscriptionDate[3] + ":" + subscriptionDate[4] + ":" + subscriptionDate[5]; 
		    	$('.subscription-date:last').text(dateStr);
			}
			
			//update pagination to the 1 page
			var pageUrl = $('#pageUrl').val();
			var res = pageUrl.split("=");
			var urlBase = res[0] + "=";
			var page = 1;
			$('#pageUrl').val(urlBase + "1");
			
			updatePagination(urlBase, page, totalPageCount);
			
			
		}).fail (function(err) {
			
			console.log(err);
			
		});
		
		
	});
	
	
	$(document).on('click', '.pager_item', function (e) {
		e.preventDefault();
		var url = $(this).data("page");
		
		var res = url.split("=");
		var urlBase = res[0] + "=";
		var page = res[1];
		
		var status = $('#subscriptionFilterSelect').find(":selected").val();
		
		console.log("url: " + urlBase + ", page: " + page + ", status: " + status);
		
		$.ajax({
	        url:url,
	        type: 'POST',
			data: {status: status},
	        dataType: 'json'
	        	
		}).done (function(data) {
			
			console.log(data);
			
			var counter = 0;
			
			//show subscriptions for the selected page
			$('.panel').each(function(i, obj) {
				
				var subscription = data[counter];
				
				if ($(obj).hasClass('hidden')) {
					
					console.log('hidden panel');
				}
				else if (subscription == null) {
					$(obj).hide();
				}
				else {
					
					$(obj).show();
					
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
					
					
					$(obj).find('.name').text(firstName + ' ' + lastName + ' - ' + productName);
					
					$(obj).find('.subscriptionID').text(id);
					
					$(obj).find('.user-id').text(userId);
					$(obj).find('.first-name').text(firstName);
					$(obj).find('.last-name').text(lastName);
					$(obj).find('.email').text(email);
					
					$(obj).find('.card-id').text(cardId);
					$(obj).find('.card-name').text(cardName);
					$(obj).find('.card-type').text(cardType);
					$(obj).find('.card-number').text(cardNumber);
					$(obj).find('.exp-date').text(expDate);
					$(obj).find('.cvv').text(cvv);

					$(obj).find('.product-id').text(productId);
					$(obj).find('.product-name').text(productName);
					$(obj).find('.product-price').text(productPrice);
					$(obj).find('.sale-price').text(productSalePrice);
					
					$(obj).find('.status').text(status);
					$(obj).find('.subscription-date').text(subscriptionDate);
					$(obj).find('.last-payment-date').text(lastPaymentDate);
					
					if (status == 'PENDING') {
						$(obj).find('.cancel-subscription').removeClass('hidden');
					}
					else {
						$(obj).find('.cancel-subscription').addClass('hidden');
					}
					
					++counter;
				}
		    	
			});
			
			//update pagination
			
			var numberOfPages = $("#numOfPages").val();
			updatePagination(urlBase, page, numberOfPages);
			
			
		}).fail (function(err) {
		    console.error(err);
		       
		});
		
	});
	
});