var deleteTransactionId = '';
var transactionPanel = new Object();
var refundTransactionId = '';
var refundTransactionPanel = new Object();

$(document).ready(function() {
	
	$(document).on('click', '#confirmAddTransaction', function(e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data('url');
		var userId = $(this).closest('.modal-content').find('[name="user"]').val();
		var cardId = $(this).closest('.modal-content').find('[name="creditCard"]').val();
		var productId = $(this).closest('.modal-content').find('[name="product"]').val();
		var amount = $(this).closest('.modal-content').find('[name="amount"]').val();
		var transactionId = $(this).closest('.modal-content').find('[name="transactionId"]').val();
		
		var form = $(this).closest('.modal-content');
		
		var transactionJSON = {userId: userId, cardId: cardId, productId: productId,
				amount: amount, transactionId: transactionId};
		
		$.ajax({
			
			type: 'POST',
			url: url,
			contentType: 'application/json',
			data: JSON.stringify(transactionJSON),
			dataType: 'json'
			
		}).done (function(data) {
			
			console.log(data);

			showSuccessAlert(data.message);
	    
	    	$('#add-transaction').modal('toggle');
			
	    	//display new transaction
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
	    	
	    	var amount = data.object.amount;
	    	var transactionId = data.object.transactionId;
	    	var status = data.object.status;
	    	
	    	console.log("amount: " + amount + ", transaction id: " + transactionId);
	    	
	    	var transactionHtml = $('.panel').html();
	    	transactionHtml = '<div class="panel panel-default">\n' + transactionHtml + "\n</div>";
	    	
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
				    			index + "</span>. " + firstName + " " + lastName + " - " + productName);
		    			$(obj).find('.subscriptionID').text(id);
				    	
		    			$(obj).find('.panel-title-text').prop('href', '#collapse' + (index - 1));
		    			$(obj).find('.collapse').prop('id', 'collapse' + (index - 1));
		    			$(obj).find('.panel-title-text').html("<span class=\"index\">" + 
				    			index + "</span>. " + firstName + " " + lastName + "(card: " + digits + ") - " + productName);
		    			$(obj).find('.transactionId').text(id);
				    	
		    			$(obj).find('.user-id').text(userId);
		    			$(obj).find('.first-name').text(firstName);
		    			$(obj).find('.last-name').text(lastName);
		    			$(obj).find('.email').text(email);
						
		    			$(obj).find('.card-id').text(cardId);
		    			$(obj).find('.card-name').text(cardName);
		    			$(obj).find('.card-type').text(cardType);
		    			$(obj).find('.card-number').text(digits);
		    			$(obj).find('.exp-date').text(expDate);
		    			$(obj).find('.cvv').text(cvv);
				    	
		    			$(obj).find('.product-id').text(productId);
		    			$(obj).find('.product-name').text(productName);
		    			$(obj).find('.product-price').text(productPrice);
		    			$(obj).find('.sale-price').text(productSalePrice);
		    			$(obj).find('.amount').text(amount);
		    			$(obj).find('.authorizeNetTransactionId').text(transactionId);
		    			$(obj).find('.status').text(status);
		    			
		    			
		    		}
		    		
		    	});
		    	
	    	}
	    	
		}).fail (function(err) {
			
			console.log(err);
			processErrors(err, form);
		})
		
	});
	
	$(document).on('click', '#editTransaction', function(e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var transactionId = $(this).closest('.edit-form').find('.transactionId').text();
		var userId = $(this).closest('.edit-form').find('.user-id').text();
		var firstName = $(this).closest('.edit-form').find('.first-name').text();
		var lastName = $(this).closest('.edit-form').find('.last-name').text();
		var cardId = $(this).closest('.edit-form').find('.card-id').text();
		var cardType = $(this).closest('.edit-form').find('.card-type').text();
		var cardNumber = $(this).closest('.edit-form').find('.card-number').text();
		var productId = $(this).closest('.edit-form').find('.product-id').text();
		var productPrice = $(this).closest('.edit-form').find('.product-price').text();
		var productName = $(this).closest('.edit-form').find('.product-name').text();
		var amount = $(this).closest('.edit-form').find('.amount').text();
		var authorizeNetTransactionId = $(this).closest('.edit-form').find('.authorizeNetTransactionId').text();
		var status = $(this).closest('.edit-form').find('.status').text();
		
		transactionPanel = $(this).closest('.edit-form');
		
		console.log(userId);
		
		$('#transactionIdModal').val(transactionId);
		
		$('#userTransactionEdit > option').each(function() {
			
			if (this.value == userId) {
				$(this).attr("selected", "selected");
			}
			
		});
		
		$('#productTransactionEdit > option').each(function() {
			
			if (this.value == productId) {
				$(this).attr("selected", "selected");
			}
			
		});
		
		
		$('#amountTransactionEdit').val(amount);
		$('#idTransactionEdit').val(authorizeNetTransactionId);
		
		$('#statusTransactionEdit > option').each(function() {
			
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
			
			$('#creditCardTransactionEdit').html(optionsHtml);
			$('#creditCardTransactionEdit').prop("disabled", false);
			
		}).fail (function(err) {
			
			console.error(err);
			
		});
		
		
		$('#edit-transaction').modal('toggle');
		
	});
	
	$(document).on('click', '#confirmEditTransaction', function(e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data('url');
		
		var id = $('#transactionIdModal').val();
		var userId = $('#userTransactionEdit').val();
		var cardId = $('#creditCardTransactionEdit').val();
		var productId = $('#productTransactionEdit').val();
		var amount = $('#amountTransactionEdit').val();
		var transactionId = $('#idTransactionEdit').val();
		var status = $('#statusTransactionEdit').val();
		
		var form = $(this).closest('.modal-content');
		
		var transactionJSON = {id: id, userId: userId, cardId: cardId, productId: productId,
				 amount: amount, transactionId: transactionId, status: status};
		
		console.log(transactionJSON);
		
		$.ajax({
			
			type: 'POST',
			url: url,
			contentType: 'application/json',
			data: JSON.stringify(transactionJSON),
			dataType: 'json'
			
		}).done (function(data) {
			
			console.log(data);
			
			showSuccessAlert(data.message);
	    
	    	$('#edit-transaction').modal('toggle');
	    	$("html, body").animate({ scrollTop: 0 }, "slow");
	    	
	    	//display edited transaction
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
	    	
	    	var amount = data.object.amount;
	    	var transactionId = data.object.transactionId;
	    	var status = data.object.status;
	    	
	    	var index = parseInt($('.index:last').text());
	    	$('.panel-title-text:last').html("<span class=\"index\">" + 
	    			index + "</span>. " + firstName + " " + lastName + "(card: " + digits + ") - " + productName);
	    	
	    	$(transactionPanel).find('.user-id').text(userId);
	    	$(transactionPanel).find('.first-name').text(firstName);
	    	$(transactionPanel).find('.last-name').text(lastName);
	    	$(transactionPanel).find('.email').text(email);
	    	
	    	$(transactionPanel).find('.card-id').text(cardId);
	    	$(transactionPanel).find('.card-name').text(cardName);
	    	$(transactionPanel).find('.card-type').text(cardType);
	    	$(transactionPanel).find('.card-number').text(digits);
	    	$(transactionPanel).find('.exp-date').text(expDate);
	    	$(transactionPanel).find('.cvv').text(cvv);
	    	
	    	$(transactionPanel).find('.product-id').text(productId);
	    	$(transactionPanel).find('.product-name').text(productName);
	    	$(transactionPanel).find('.product-price').text(productPrice);
	    	$(transactionPanel).find('.sale-price').text(productSalePrice);
	    	
	    	$(transactionPanel).find('.amount').text(amount);
	    	$(transactionPanel).find('.authorizeNetTransactionId').text(authorizeNetTransactionId);
	    	$(transactionPanel).find('.status').text(status);
	    	
		}).fail (function(err) {
			
			console.log(err);
			processErrors(err, form);
		})
		
	});
	
	$('#deleteTransaction').on('show.bs.modal', function (e) {
	    var trigger = $(e.relatedTarget);
	    var id = $(trigger).closest('.edit-form').find('.transactionId').text();
	    
	    deleteTransactionId = id;
	    transactionPanel = $(trigger).closest('.panel');
	});
	
	$(document).on('click', '#confirmDeleteTransaction', function(e) {
		
		e.preventDefault();
		
		var url = $(this).data('url');
		var id = deleteTransactionId;
		var panel = transactionPanel; 
		
		$.ajax({
			
			type: 'POST',
			url: url,
			data: {id: id},
			dataType: 'json'
			
		}).done (function(data) {
			
			console.log(data);
			
			showSuccessAlert(data.message);

	    	$(panel).hide();
	    	
	    	$('#deleteTransaction').modal('toggle');
	    	
	    	$("html, body").animate({ scrollTop: 0 }, "slow");
			
		}).fail (function(err) {
			
			console.error(err);
			
		});
		
	});
	
	$('#refundTransaction').on('show.bs.modal', function (e) {
	    var trigger = $(e.relatedTarget);
	    var id = $(trigger).closest('.edit-form').find('.transactionId').text();
	    
	    refundTransactionId = id;
	    refundTransactionPanel = $(trigger).closest('.panel');
	});
	
	$(document).on('click', '#confirmRefundTransaction', function(e) {
		
		e.preventDefault();
		
		var url = $(this).data('url');
		var id = refundTransactionId;
		var panel = refundTransactionPanel; 
		
		$.ajax({
			
			type: 'POST',
			url: url,
			data: {id: id},
			dataType: 'json'
			
		}).done (function(data) {
			
			console.log(data);
			
			showSuccessAlert(data.message);
	    
	    	//$(panel).remove();
			$(panel).find('.status').text('REFUNDED');
			$(panel).find('.refund-btn').addClass('hidden');
	    	
	    	$('#refundTransaction').modal('toggle');
	    	
	    	$("html, body").animate({ scrollTop: 0 }, "slow");
			
		}).fail (function(err) {
			
			console.error(err);
			showErrorAlert(err.responseJSON.errorMessage);
			
			$('#refundTransaction').modal('toggle');
	    	
	    	$("html, body").animate({ scrollTop: 0 }, "slow");
			
			
		});
		
	});
	
	
	$(document).on('click', '.pager_item', function (e) {
		e.preventDefault();
		var url = $(this).data("page");
		
		var res = url.split("=");
		var urlBase = res[0] + "=";
		var page = res[1];
		
		$.ajax({
	        url:url,
	        type: 'GET',
	        dataType: 'json'
	        	
		}).done (function(data) {
			
			console.log(data);
			
			var counter = 0;

			var transactions = data.object;
			
			//show transactions for the selected page
			$('.panel').each(function(i, obj) {
				
				var transaction = transactions[counter];
				
				if ($(obj).hasClass('hidden')) {
					
					console.log('hidden panel');
				}
				else if (transaction == null) {
					$(obj).hide();
				}
				else {
					
					$(obj).show();
					
					var id = transaction.id;
					var amount = transaction.amount;
					var transactionId = transaction.transactionId;
					var status = transaction.status;
					
					//user properties
					var userId = transaction.user.id;
					var firstName = transaction.user.firstName;
					var lastName = transaction.user.lastName;
					var userId = transaction.user.id;
					var email = transaction.user.email;
					
					//credit card properties
					var cardId = transaction.creditCard.id;
					var cardName = transaction.creditCard.name;
					var cardType = transaction.creditCard.cardType;
					var cardNumber = transaction.creditCard.digits;
					var expDate = transaction.creditCard.expDate;
					var cvv = transaction.creditCard.cvv;
					
					//product properties
					var productId = transaction.product.id;
					var productName = transaction.product.name;
					var productPrice = transaction.product.price;
					var productSalePrice = transaction.product.salePrice;
					
					
					$(obj).find('.name').text(firstName + ' ' + lastName + ' (card: ' + cardNumber + ') - ' + productName);
					
					$(obj).find('.transactionId').text(id);
					
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
					$(obj).find('.amount').text(amount);
					$(obj).find('.authorizeNetTransactionId').text(transactionId);
					
					++counter;
				}
		    	
			});
			
			//update pagination
			var numberOfPages = data.totalPageCount;
			updatePagination(urlBase, page, numberOfPages);
			
			
		}).fail (function(err) {
		    console.error(err);
		       
		});
		
	});
	
	
});