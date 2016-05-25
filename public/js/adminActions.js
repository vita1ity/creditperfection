var deleteUserId = '';
var deletePanel = new Object();
var deleteProductId = '';
var deleteProductPanel = new Object();
var deleteCreditCardId = '';
var deleteCreditCardPanel = new Object();
var deleteTransactionId = '';
var deleteTransactionPanel = new Object();
var deleteAuthNetAccountId = '';
var deleteAuthNetAccountPanel = new Object();

window.onload = function(){
	
}

$(document).ready(function() {
	
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
	    	userHtml = '<div class="panel panel-default">\n' + userHtml + "\n</div>";
	    	
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
	    	$('.active:last').prop("checked", false);
	    	
	    	
	    	
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
		if ($(this).closest('.edit-form').find('[name="active"]').is(':checked')) {
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
	    deletePanel = $(trigger).closest('.panel');
	});
	
	$(document).on('click', '#confirmDeleteUser', function (e) {
		
		e.preventDefault();
		
		var url = $(this).data("url");
		
		var id = deleteUserId;
		var panel = deletePanel;
		
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
	
	
	$(document).on('click', '#confirmAddProduct', function (e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data("url");
		var name = $(this).closest('.modal-content').find('[name="name"]').val();
		var price = $(this).closest('.modal-content').find('[name="price"]').val();
		var salePrice = $(this).closest('.modal-content').find('[name="salePrice"]').val();
		
		var form = $(this).closest('.modal-content');
		
		if (!validateProduct(this)) {
			return;
		}
		
		var productJSON =  {'name': name, 'price': price, 'salePrice': salePrice};
		
		console.log(url);
		console.log(productJSON);
		
		$.ajax({
			
	        type: 'POST',
	        url: url, 
	        contentType: 'application/json',
	        data: JSON.stringify(productJSON),
	        dataType: 'json'
		
	    }).done (function(data) {
	    	
	    	console.log(data);
	    	
	    	showSuccessAlert(data.message);
	    
	    	$('#add-product').modal('toggle');
	    	
	    	//display new product
	    	var id = data.id;
	    	
	    	var productHtml = $('.panel').html();
	    	productHtml = '<div class="panel panel-default">\n' + productHtml + "\n</div>";
	    	
	    	var index = parseInt($('.index:last').text()) + 1;
	    	
	    	$('#accordion').append(productHtml);
	    	
	    	console.log("index: " + index);
	    	$('.panel-title-text:last').prop('href', '#collapse' + (index - 1));
	    	$('.collapse:last').prop('id', 'collapse' + (index - 1));
	    	$('.panel-title-text:last').html("<span class=\"index\">" + 
	    			index + "</span>. " + name);
	    	$('.productID:last').text(id);
	    	
	    	$('.name').eq(-2).val(name);
	    	
	    	$('.price').eq(-2).val(price);
	    	$('.sale-price').eq(-2).val(salePrice);
	    	
	    	
	    }).fail (function(err) {
			
	    	console.error(err);
			processErrors(err, form);
			
	    });
	});
	
	$(document).on('click', '#editProduct', function (e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data("url");
		
		var id = $(this).closest('.edit-form').find('.productID').text();
		var name = $(this).closest('.edit-form').find('[name="name"]').val();
		var price = $(this).closest('.edit-form').find('[name="price"]').val();
		var salePrice = $(this).closest('.edit-form').find('[name="salePrice"]').val();
		
		var form = $(this).closest('.edit-form');
		
		if (!validateProduct($(this))) {
			return;
		}
		
		var productJSON =  {'id': id, 'name': name, 'price': price, 'salePrice': salePrice};
		
		console.log(url);
		console.log(productJSON);
		
		$.ajax({
			
	        type: 'POST',
	        url: url, 
	        contentType: 'application/json',
	        data: JSON.stringify(productJSON),
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
	
	$('#deleteProduct').on('show.bs.modal', function (e) {
	    var trigger = $(e.relatedTarget);
	    var id = $(trigger).closest('.edit-form').find('.productID').text();
	    
	    deleteProductId = id;
	    deleteProductPanel = $(trigger).closest('.panel');
	});
	
	$(document).on('click', '#confirmDeleteProduct', function (e) {
		
		e.preventDefault();
		
		var url = $(this).data("url");
		
		var id = deleteProductId;
		var panel = deleteProductPanel;
		
		$.ajax({
			
	        type: 'POST',
	        url: url,
	        data: {"id": id},
	        dataType: 'json'
		
	    }).done (function(data) {
	    	
	    	console.log(data);

	    	showSuccessAlert(data.message);
	    	
	    	$(panel).remove();
	    	
	    	$('#deleteProduct').modal('toggle');
	    	
	    	$("html, body").animate({ scrollTop: 0 }, "slow");
	    	
	    	
	    
	    }).fail (function(err) {
			
	    	console.error(err)
			
	    });
		
	});
	

	function validateProduct(ref) {
		
		var validated = true;
		$(ref).closest('.validate-product').find('.form-input').each(function(i, obj) {
			
			var val = $(obj).val();
			if (val == "") {
				var errorsHtml = $(obj).parent().find('.error').html();
				
				errorsHtml += "Field is required" + "</br>";
				
				$(obj).parent().find('.error').html(errorsHtml);
				validated = false;
			}
			
		});
		
		var price = $(ref).closest('.validate-product').find('[name="price"]').val();
		
		if(!price.match(/^-?(\d*\.)?\d*$/)) {
			var errorsHtml = $(ref).closest('.validate-product').find('.price-error').html();
			errorsHtml += "Price should be numeric value" + "</br>";
			$(ref).closest('.validate-product').find('.price-error').html(errorsHtml);
			validated = false;
		
		}
		else if (price < 0) {
			var errorsHtml = $(ref).closest('.validate-product').find('.price-error').html();
			errorsHtml += "Price should be gteater than 0" + "</br>";
			$(ref).closest('.validate-product').find('.price-error').html(errorsHtml);
			validated = false;
		}
		
		var salePrice = $(ref).closest('.validate-product').find('[name="salePrice"]').val();
		
		if(!salePrice.match(/^-?(\d*\.)?\d*$/)) {
			var errorsHtml = $(ref).closest('.validate-product').find('.salePrice-error').html();
			errorsHtml += "Sale Price should be numeric value" + "</br>";
			$(ref).closest('.validate-product').find('.salePrice-error').html(errorsHtml);
			validated = false;
		}
		else if (salePrice < 0) {
			var errorsHtml = $(ref).closest('.validate-product').find('.salePrice-error').html();
			errorsHtml += "Sale Price should be greater than 0" + "</br>";
			$(ref).closest('.validate-product').find('.salePrice-error').html(errorsHtml);
			validated = false;
		}
		return validated;
	}
	
	$(document).on('click', '#confirmAddCreditCard', function (e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data("url");
		var name = $(this).closest('.modal-content').find('[name="name"]').val();
		var cardType = $(this).closest('.modal-content').find('[name="cardType"]').val();
		var digits = $(this).closest('.modal-content').find('[name="digits"]').val();
		var month = $(this).closest('.modal-content').find('[name="month"]').val();
		var year = $(this).closest('.modal-content').find('[name="year"]').val();
		var cvv = $(this).closest('.modal-content').find('[name="cvv"]').val();
		var ownerId = $(this).closest('.modal-content').find('[name="owner"]').val();
		
		var form = $(this).closest('.modal-content');
		
		var creditCardJSON =  {name: name, cardType: cardType, digits: digits, month: month,
				year: year, cvv: cvv, ownerId: ownerId};
		
		if (!validateCreditCard(this)) {
			console.log("Credit card validation failed");
			return;
		}
		
		console.log(creditCardJSON);
		
		$.ajax({
			
	        type: 'POST',
	        url: url, 
	        contentType: 'application/json',
	        data: JSON.stringify(creditCardJSON),
	        dataType: 'json'
		
	    }).done (function(data) {
	    	
	    	console.log(data);
	    	
	    	showSuccessAlert(data.message);
	    
	    	$('#add-credit-card').modal('toggle');
	    	
	    	//display new credit card
	    	var id = data.id;
	    	
	    	var creditCardHtml = $('.panel').html();
	    	creditCardHtml = '<div class="panel panel-default">\n' + creditCardHtml + "\n</div>";
	    	
	    	var index = parseInt($('.index:last').text()) + 1;
	    	
	    	$('#accordion').append(creditCardHtml);
	    	
	    	console.log("index: " + index);
	    	$('.panel-title-text:last').prop('href', '#collapse' + (index - 1));
	    	$('.collapse:last').prop('id', 'collapse' + (index - 1));
	    	$('.panel-title-text:last').html("<span class=\"index\">" + 
	    			index + "</span>. " + name + " - " + digits);
	    	$('.cardID:last').text(id);
	    	
	    	$('.name').eq(-1).val(name);
	    	
	    	$('.card-type:last > option').each(function(i, obj) {
	    		
	    		if (cardType == $(obj).val()) {
	    			$(obj).prop("selected", true);
	    		}
	    		else {
	    			$(obj).prop("selected", false);
	    		}
	    	});
	    	
	    	$('.digits').eq(-1).val(digits);
	    	$('.cvv').eq(-1).val(cvv);
	    	
	    	$('.month:last > option').each(function(i, obj) {
	    		
	    		
	    		if (month == $(obj).val()) {
	    			$(obj).prop("selected", true);
	    		}
	    		else {
	    			$(obj).prop("selected", false);
	    		}
	    	});
	    	$('.year:last > option').each(function(i, obj) {
	    		
	    		if (year == $(obj).val()) {
	    			$(obj).prop("selected", true);
	    		}
	    		else {
	    			$(obj).prop("selected", false);
	    		}
	    	});
	    	
	    	
	    }).fail (function(err) {
			
	    	console.error(err);
			processErrors(err, form);
			
	    });
	});
	
	$(document).on('click', '#editCreditCard', function (e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data("url");
		
		var id = $(this).closest('.edit-form').find('.cardID').text();
		var name = $(this).closest('.edit-form').find('[name="name"]').val();
		var cardType = $(this).closest('.edit-form').find('[name="cardType"]').val();
		var digits = $(this).closest('.edit-form').find('[name="digits"]').val();
		var month = $(this).closest('.edit-form').find('[name="month"]').val();
		var year = $(this).closest('.edit-form').find('[name="year"]').val();
		var cvv = $(this).closest('.edit-form').find('[name="cvv"]').val();
		
		var form = $(this).closest('.edit-form');
		
		var creditCardJSON =  {id: id, name: name, cardType: cardType, digits: digits, month: month,
				year: year, cvv: cvv};
		
		if (!validateCreditCard(this)) {
			console.log("Credit card validation failed");
			return;
		}
		
		console.log(creditCardJSON);
		
		$.ajax({
			
	        type: 'POST',
	        url: url, 
	        contentType: 'application/json',
	        data: JSON.stringify(creditCardJSON),
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
	
	$('#deleteCreditCard').on('show.bs.modal', function (e) {
	    var trigger = $(e.relatedTarget);
	    var id = $(trigger).closest('.edit-form').find('.cardID').text();
	    
	    deleteCreditCardId = id;
	    deleteCreditCardPanel = $(trigger).closest('.panel');
	});
	
	$(document).on('click', '#confirmDeleteCreditCard', function (e) {
		
		e.preventDefault();
		
		var url = $(this).data("url");
		
		var id = deleteCreditCardId;
		var panel = deleteCreditCardPanel;
		
		console.log("url: " + url + ", id: " + id);
		
		$.ajax({
			
	        type: 'POST',
	        url: url,
	        data: {"id": id},
	        dataType: 'json'
		
	    }).done (function(data) {
	    	
	    	console.log(data);

	    	showSuccessAlert(data.message);
	
	    	$(panel).remove();
	    	$('#deleteCreditCard').modal('toggle');
	    	
	    	$("html, body").animate({ scrollTop: 0 }, "slow");
	    	
	    	
	    
	    	
	    }).fail (function(err) {
			
	    	console.error(err)
			
	    });
		
	});
	
	
	function validateCreditCard(ref) {
		var validated = true;
		$(ref).closest(".card-validation").find('.form-input').each(function(i, obj) {
			
			var val = $(obj).val();
			if (val == "") {
				var errorsHtml = $(obj).parent().find('.error').html();
				
				errorsHtml += "Field is required" + "</br>";
				
				$(obj).parent().find('.error').html(errorsHtml);
				validated = false;
			}
			
		});
		
		var cvv = $(ref).closest(".card-validation").find('[name="cvv"]').val();
		
		if(!cvv.match(/^\d+$/)) {
			var errorsHtml = $(ref).closest(".card-validation").find('.cvv-error').html();
			errorsHtml += "CVV should contain only digits" + "</br>";
			$(ref).closest(".card-validation").find('.cvv-error').html(errorsHtml);
			validated = false;
		}
		return validated;
	}
	
	//TRANSACTIONS
	$(document).on('change', '.user-cards', function (e) {
		
		e.preventDefault();
		
		var url = $(this).data('url');
		var userId = $(this).find(":selected").val();
		
		var userHtml = $(this);
		
		$.ajax({
			
			type: 'GET',
	        url: url,
	        data: {'userId': userId},
	        dataType: 'json'
			
		}).done (function(data) {
			
			console.log(data);
			
			var optionsHtml = '<option value=\"\">Select Card Owner</option>\n';
			for (var i = 0; i < data.length; i++) {
				var creditCard = data[i];
				optionsHtml += "<option value=\"" + creditCard.id + "\"> " + (i + 1) + ". " + creditCard.cardType + 
					": " + creditCard.digits + " </option> \n";
				
			}
			
			$(userHtml).closest('.modal-body').find('.credit-card').html(optionsHtml);
			$(userHtml).closest('.modal-body').find('.credit-card').prop("disabled", false);
			
		}).fail (function(err) {
			
			console.error(err);
			
		});
		
	});
	
	
	$(document).on('click', '#confirmAddTransaction', function(e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data('url');
		var userId = $(this).closest('.modal-content').find('[name="user"]').val();
		var cardId = $(this).closest('.modal-content').find('[name="creditCard"]').val();
		var productId = $(this).closest('.modal-content').find('[name="product"]').val();
		
		var form = $(this).closest('.modal-content');
		
		var transactionJSON = {userId: userId, cardId: cardId, productId: productId};
		
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
	    	var id = data.transaction.id;
	    	
	    	var userId = data.transaction.user.id;
	    	var firstName = data.transaction.user.firstName;
	    	var lastName = data.transaction.user.lastName;
	    	var email = data.transaction.user.email;
	    	
	    	var cardId = data.transaction.creditCard.id;
	    	var cardName = data.transaction.creditCard.name;
	    	var cardType = data.transaction.creditCard.cardType;
	    	var digits = data.transaction.creditCard.digits;
	    	var expDate = data.transaction.creditCard.expDate;
	    	var cvv = data.transaction.creditCard.cvv;
	    	
	    	var productId = data.transaction.product.id;
	    	var productName = data.transaction.product.name;
	    	var productPrice = data.transaction.product.price;
	    	var productSalePrice = data.transaction.product.salePrice;
	    	
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
		
		var transactionId = $('#transactionIdModal').val();
		var userId = $('#userTransactionEdit').val();
		var cardId = $('#creditCardTransactionEdit').val();
		var productId = $('#productTransactionEdit').val();
		
		var form = $(this).closest('.modal-content');
		
		var transactionJSON = {transactionId: transactionId, userId: userId, cardId: cardId, productId: productId};
		
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
	    	var userId = data.transaction.user.id;
	    	var firstName = data.transaction.user.firstName;
	    	var lastName = data.transaction.user.lastName;
	    	var email = data.transaction.user.email;
	    	
	    	var cardId = data.transaction.creditCard.id;
	    	var cardName = data.transaction.creditCard.name;
	    	var cardType = data.transaction.creditCard.cardType;
	    	var digits = data.transaction.creditCard.digits;
	    	var expDate = data.transaction.creditCard.expDate;
	    	var cvv = data.transaction.creditCard.cvv;
	    	
	    	var productId = data.transaction.product.id;
	    	var productName = data.transaction.product.name;
	    	var productPrice = data.transaction.product.price;
	    	var productSalePrice = data.transaction.product.salePrice;
	    	
	    	var index = parseInt($('.index:last').text());
	    	$('.panel-title-text:last').html("<span class=\"index\">" + 
	    			index + "</span>. " + firstName + " " + lastName + "(card: " + digits + ") - " + productName);
	    	
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
	    	
			
		}).fail (function(err) {
			
			console.log(err);
			processErrors(err, form);
		})
		
	});
	
	$('#deleteTransaction').on('show.bs.modal', function (e) {
	    var trigger = $(e.relatedTarget);
	    var id = $(trigger).closest('.edit-form').find('.transactionId').text();
	    
	    deleteTransactionId = id;
	    deleteTransactionPanel = $(trigger).closest('.panel');
	});
	
	$(document).on('click', '#confirmDeleteTransaction', function(e) {
		
		e.preventDefault();
		
		var url = $(this).data('url');
		var id = deleteTransactionId;
		var panel = deleteTransactionPanel; 
		
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
	
	
	
	$(document).on('click', '#confirmAddAuthNetAccount', function(e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data('url');
		
		var name = $(this).closest('.modal-content').find('.name').val();
		var description = $(this).closest('.modal-content').find('.description').val();
		var loginId = $(this).closest('.modal-content').find('.login-id').val();
		var transactionKey = $(this).closest('.modal-content').find('.transaction-key').val();
		
		var accountJSON = {name: name, description: description, loginId: loginId, transactionKey: transactionKey};
		
		var form = $(this).closest('.modal-content');
		
		$.ajax ({
			type: 'POST',
	        url: url, 
	        contentType: 'application/json',
	        data: JSON.stringify(accountJSON),
	        dataType: 'json'	
		}).done (function(data) {
			
			console.log(data);
			
			$('#addAuthNetAccount').modal('toggle');
			
			showSuccessAlert(data.message);
	    
	    	$('#add-user').modal('toggle');
	    	
	    	//display new merchant account
	    	var id = data.id;
	    	
	    	var accountHtml = $('.panel').html();
	    	accountHtml = '<div class="panel panel-default">\n' + accountHtml + "\n</div>";
	    	
	    	var index = parseInt($('.index:last').text()) + 1;
	    	
	    	if (isNaN(index)) {
	    		
	    		index = 1;
	    	}
	    	
	    	$('#accordion').append(accountHtml);
	    	
	    	$('.panel-title-text:last').prop('href', '#collapse' + (index - 1));
	    	$('.collapse:last').prop('id', 'collapse' + (index - 1));
	    	$('.panel-title-text:last').html("<span class=\"index\">" + 
	    			index + "</span>. " + name);
	    	$('.authNetAccountId:last').text(id);
	    	
	    	$('.name:last').val(name);
	    	
	    	$('.description:last').val(description);
	    	$('.login-id:last').val(loginId);
	    	$('.transaction-key:last').val(transactionKey);
	    	
			
		}).fail (function(err) {
			console.error(err);
			processErrors(err, form);
		});
		
	});
	
	$(document).on('click', '#editAuthNetAccount', function(e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data('url');
		
		var id = $(this).closest('.edit-form').find('.authNetAccountId').text();
		var name = $(this).closest('.edit-form').find('.name').val();
		var description = $(this).closest('.edit-form').find('.description').val();
		var loginId = $(this).closest('.edit-form').find('.login-id').val();
		var transactionKey = $(this).closest('.edit-form').find('.transaction-key').val();
		
		var accountJSON = {id: id, name: name, description: description, loginId: loginId, transactionKey: transactionKey};
		
		var form = $(this).closest('.edit-form');
		
		$.ajax({
			type: 'POST',
			url: url,
			contentType: 'application/json',
			data: JSON.stringify(accountJSON),
			dataType: 'json'
		}).done (function(data) {
			
			showSuccessAlert(data.message);
		
	    	$("html, body").animate({ scrollTop: 0 }, "slow");
			
		}).fail (function(err) {
			console.error(err);
			processErrors(err, form);
		});
		
		
	});
	
	$('#deleteAuthNetAccount').on('show.bs.modal', function (e) {
	    var trigger = $(e.relatedTarget);
	    var id = $(trigger).closest('.edit-form').find('.authNetAccountId').text();
	    
	    deleteAuthNetAccountId = id;
	    deleteAuthNetAccountPanel = $(trigger).closest('.panel');
	});
	
	$(document).on('click', '#confirmDeleteAuthNetAccount', function(e) {
		
		e.preventDefault();
		
		var url = $(this).data('url');
		
		var id = deleteAuthNetAccountId;
		var deletePanel = deleteAuthNetAccountPanel;
		
		$.ajax({
			type: 'POST',
			url: url,
			data: {id: id},
			dataType: 'json'
		}).done (function(data) {
			
			showSuccessAlert(data.message);
			
			$(deletePanel).remove();
	    	
	    	$('#deleteAuthNetAccount').modal('toggle');
	    	
	    	$("html, body").animate({ scrollTop: 0 }, "slow");
			
		}).fail (function(err) {
			console.error(err);
		});
		
	});
	
	//remove error messages
	function clearErrors() {
		
		$('.error').each(function(i, obj) {
			$(obj).html("");
		});
	}
	
	function processErrors(err, form) { 
		for (var i = 0; i < err.responseJSON.length; i++) {
			var error = err.responseJSON[i];
			var field = error.field;
			var errorMessage = error.error;
			
			$(form).find('.form-input').each(function(i, obj) {
				
				if (field == $(obj).attr('name')) {
					var errorsHtml = $(obj).parent().find('.error').html();
					
					errorsHtml += errorMessage + "</br>";
					
					$(obj).parent().find('.error').html(errorsHtml);
					
				}
			});
			
		}
	}
	
	function showSuccessAlert(message) {
		var alertHtml = "";
    	alertHtml += "<div class=\"alert alert-success\">\n";
    	alertHtml += "<a href=\"#\" class=\"close\" data-dismiss=\"alert\" aria-label=\"close\">&times;</a>\n";
    	alertHtml += "<span id=\"alert-message\">" + message + "</span>\n"	
    		
    	$('#alert-box').html(alertHtml);
	}
	
});

