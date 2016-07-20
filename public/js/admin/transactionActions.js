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
	    	
	    	var index = parseInt($('.index:last').text()) + 1;
	    	
	    	$('#accordion').append(transactionHtml);
	    	
	    	console.log("index: " + index);
	    	$('.panel-title-text:last').prop('href', '#collapse' + (index - 1));
	    	$('.collapse:last').prop('id', 'collapse' + (index - 1));
	    	$('.panel-title-text:last').html("<span class=\"index\">" + 
	    			index + "</span>. " + firstName + " " + lastName + "(card: " + digits + ") - " + productName);
	    	$('.transactionId:last').text(id);
	    	
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
	    	$('.amount:last').text(amount);
	    	$('.authorizeNetTransactionId:last').text(transactionId);
	    	$('.status:last').text(status);
	    	
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
	    
	    	$(panel).remove();
	    	
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
	
});