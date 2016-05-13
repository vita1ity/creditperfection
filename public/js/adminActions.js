window.onload = function(){
	
}

$(document).ready(function() {
	
	$(document).on('click', '#confirmAdd', function (e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data("url");
		var firstName = $(this).parent().parent().find('[name="firstName"]').val();
		var lastName = $(this).parent().parent().find('[name="lastName"]').val();
		var email = $(this).parent().parent().find('[name="email"]').val();
		var address = $(this).parent().parent().find('[name="address"]').val();
		var city = $(this).parent().parent().find('[name="city"]').val();
		var state = $(this).parent().parent().find('[name="state"]').val();
		var zip = $(this).parent().parent().find('[name="zip"]').val();
		var password = $(this).parent().parent().find('[name="password"]').val();
		
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
	    	
	    	var alertHtml = "";
	    	alertHtml += "<div class=\"alert alert-success\">\n";
	    	alertHtml += "<a href=\"#\" class=\"close\" data-dismiss=\"alert\" aria-label=\"close\">&times;</a>\n";
	    	alertHtml += "<span id=\"alert-message\">" + data.message + "</span>\n"	
	    		
	    	$('#alert-box').html(alertHtml);
	    
	    	$('#add-user').modal('toggle');
	    	
	    	
	    }).fail (function(err) {
			
	    	console.error(err);
			processErrors(err);
			
	    });
	});
	
	$(document).on('click', '#editUser', function (e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data("url");
		var id = $(this).parent().parent().parent().find('.userID').text();
		var firstName = $(this).parent().parent().parent().find('[name="firstName"]').val();
		var lastName = $(this).parent().parent().parent().find('[name="lastName"]').val();
		var email = $(this).parent().parent().parent().find('[name="email"]').val();
		var address = $(this).parent().parent().parent().find('[name="address"]').val();
		var city = $(this).parent().parent().parent().find('[name="city"]').val();
		var state = $(this).parent().parent().parent().find('[name="state"]').val();
		var zip = $(this).parent().parent().parent().find('[name="zip"]').val();
		var password = $(this).parent().parent().parent().find('[name="password"]').val();
		//var activeStr = $(this).parent().parent().parent().find('[name="active"]').val();
		
		var active = false;
		if ($(this).parent().parent().parent().find('[name="active"]').is(':checked')) {
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
	    	var alertHtml = "";
	    	alertHtml += "<div class=\"alert alert-success\">\n";
	    	alertHtml += "<a href=\"#\" class=\"close\" data-dismiss=\"alert\" aria-label=\"close\">&times;</a>\n";
	    	alertHtml += "<span id=\"alert-message\">" + data.message + "</span>\n"	
	    		
	    	$('#alert-box').html(alertHtml);
	    
	    	$("html, body").animate({ scrollTop: 0 }, "slow");
	    	
	    }).fail (function(err) {
			
	    	console.error(err);
			processErrors(err);
			
	    });
	});
	
	$(document).on('click', '#deleteUser', function (e) {
		
		e.preventDefault();
		
		var url = $(this).data("url");
		var id = $(this).parent().parent().parent().find('.userID').text();
		
		console.log("url: " + url + ", id: " + id);
		
		$.ajax({
			
	        type: 'POST',
	        url: url,
	        data: {"id": id},
	        dataType: 'json'
		
	    }).done (function(data) {
	    	
	    	console.log(data);
	    	var alertHtml = "";
	    	alertHtml += "<div class=\"alert alert-success\">\n";
	    	alertHtml += "<a href=\"#\" class=\"close\" data-dismiss=\"alert\" aria-label=\"close\">&times;</a>\n";
	    	alertHtml += "<span id=\"alert-message\">" + data.message + "</span>\n"	
	    		
	    	$('#alert-box').html(alertHtml);
	    
	    }).fail (function(err) {
			
	    	console.error(err);
			
	    });
		
	});
	
	
	$(document).on('click', '#confirmAddProduct', function (e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data("url");
		var name = $(this).parent().parent().find('[name="name"]').val();
		var price = $(this).parent().parent().find('[name="price"]').val();
		var salePrice = $(this).parent().parent().find('[name="salePrice"]').val();
		
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
	    	
	    	var alertHtml = "";
	    	alertHtml += "<div class=\"alert alert-success\">\n";
	    	alertHtml += "<a href=\"#\" class=\"close\" data-dismiss=\"alert\" aria-label=\"close\">&times;</a>\n";
	    	alertHtml += "<span id=\"alert-message\">" + data.message + "</span>\n"	
	    		
	    	$('#alert-box').html(alertHtml);
	    
	    	$('#add-product').modal('toggle');
	    	
	    	
	    }).fail (function(err) {
			
	    	console.error(err);
			processErrors(err);
			
	    });
	});
	
	$(document).on('click', '#editProduct', function (e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data("url");
		
		var id = $(this).parent().parent().parent().find('.productID').text();
		var name = $(this).parent().parent().parent().find('[name="name"]').val();
		var price = $(this).parent().parent().parent().find('[name="price"]').val();
		var salePrice = $(this).parent().parent().parent().find('[name="salePrice"]').val();
		
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
	    	
	    	var alertHtml = "";
	    	alertHtml += "<div class=\"alert alert-success\">\n";
	    	alertHtml += "<a href=\"#\" class=\"close\" data-dismiss=\"alert\" aria-label=\"close\">&times;</a>\n";
	    	alertHtml += "<span id=\"alert-message\">" + data.message + "</span>\n"	
	    		
	    	$('#alert-box').html(alertHtml);
	    
	    	$("html, body").animate({ scrollTop: 0 }, "slow");
	    	
	    	
	    }).fail (function(err) {
			
	    	console.error(err);
			processErrors(err);
			
	    });
	});
	
	$(document).on('click', '#deleteProduct', function (e) {
		
		e.preventDefault();
		
		var url = $(this).data("url");
		var id = $(this).parent().parent().parent().find('.productID').text();
		
		console.log("url: " + url + ", id: " + id);
		
		$.ajax({
			
	        type: 'POST',
	        url: url,
	        data: {"id": id},
	        dataType: 'json'
		
	    }).done (function(data) {
	    	
	    	console.log(data);
	    	var alertHtml = "";
	    	alertHtml += "<div class=\"alert alert-success\">\n";
	    	alertHtml += "<a href=\"#\" class=\"close\" data-dismiss=\"alert\" aria-label=\"close\">&times;</a>\n";
	    	alertHtml += "<span id=\"alert-message\">" + data.message + "</span>\n"	
	    		
	    	$('#alert-box').html(alertHtml);
	    
	    }).fail (function(err) {
			
	    	console.error(err)
			
	    });
		
	});
	

	function validateProduct(ref) {
		
		var validated = true;
		$(ref).parent().parent().parent().find('.form-input').each(function(i, obj) {
			
			var val = $(obj).val();
			if (val == "") {
				var errorsHtml = $(obj).parent().find('.error').html();
				
				errorsHtml += "Field is required" + "</br>";
				
				$(obj).parent().find('.error').html(errorsHtml);
				validated = false;
			}
			
		});
		
		var price = $(ref).parent().parent().parent().find('[name="price"]').val();
		
		if(!price.match(/^-?(\d*\.)?\d*$/)) {
			var errorsHtml = $(ref).parent().parent().parent().find('.price-error').html();
			errorsHtml += "Price should be numeric value" + "</br>";
			$(ref).parent().parent().parent().find('.price-error').html(errorsHtml);
			validated = false;
		
		}
		else if (price < 0) {
			var errorsHtml = $(ref).parent().parent().parent().find('.price-error').html();
			errorsHtml += "Price should be gteater than 0" + "</br>";
			$(ref).parent().parent().parent().find('.price-error').html(errorsHtml);
			validated = false;
		}
		
		var salePrice = $(ref).parent().parent().parent().find('[name="salePrice"]').val();
		
		if(!salePrice.match(/^-?(\d*\.)?\d*$/)) {
			var errorsHtml = $(ref).parent().parent().parent().find('.salePrice-error').html();
			errorsHtml += "Sale Price should be numeric value" + "</br>";
			$(ref).parent().parent().parent().find('.salePrice-error').html(errorsHtml);
			validated = false;
		}
		else if (salePrice < 0) {
			var errorsHtml = $(ref).parent().parent().parent().find('.salePrice-error').html();
			errorsHtml += "Sale Price should be greater than 0" + "</br>";
			$(ref).parent().parent().parent().find('.salePrice-error').html(errorsHtml);
			validated = false;
		}
		return validated;
	}
	
	$(document).on('click', '#confirmAddCreditCard', function (e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data("url");
		var name = $(this).parent().parent().find('[name="name"]').val();
		var cardType = $(this).parent().parent().find('[name="cardType"]').val();
		var digits = $(this).parent().parent().find('[name="digits"]').val();
		var month = $(this).parent().parent().find('[name="month"]').val();
		var year = $(this).parent().parent().find('[name="year"]').val();
		var cvv = $(this).parent().parent().find('[name="cvv"]').val();
		var ownerId = $(this).parent().parent().find('[name="owner"]').val();
		
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
	    	
	    	var alertHtml = "";
	    	alertHtml += "<div class=\"alert alert-success\">\n";
	    	alertHtml += "<a href=\"#\" class=\"close\" data-dismiss=\"alert\" aria-label=\"close\">&times;</a>\n";
	    	alertHtml += "<span id=\"alert-message\">" + data.message + "</span>\n"	
	    		
	    	$('#alert-box').html(alertHtml);
	    
	    	$('#add-credit-card').modal('toggle');
	    	
	    	
	    }).fail (function(err) {
			
	    	console.error(err);
			processErrors(err);
			
	    });
	});
	
	$(document).on('click', '#editCreditCard', function (e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data("url");
		
		var id = $(this).parent().parent().parent().find('.cardID').text();
		var name = $(this).parent().parent().parent().find('[name="name"]').val();
		var cardType = $(this).parent().parent().parent().find('[name="cardType"]').val();
		var digits = $(this).parent().parent().parent().find('[name="digits"]').val();
		var month = $(this).parent().parent().parent().find('[name="month"]').val();
		var year = $(this).parent().parent().parent().find('[name="year"]').val();
		var cvv = $(this).parent().parent().parent().find('[name="cvv"]').val();
		
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
	    	
	    	var alertHtml = "";
	    	alertHtml += "<div class=\"alert alert-success\">\n";
	    	alertHtml += "<a href=\"#\" class=\"close\" data-dismiss=\"alert\" aria-label=\"close\">&times;</a>\n";
	    	alertHtml += "<span id=\"alert-message\">" + data.message + "</span>\n"	
	    		
	    	$('#alert-box').html(alertHtml);
	    
	    	$("html, body").animate({ scrollTop: 0 }, "slow");
	    	
	    	
	    }).fail (function(err) {
			
	    	console.error(err);
			processErrors(err);
			
	    });
	});
	
	$(document).on('click', '#deleteCreditCard', function (e) {
		
		e.preventDefault();
		
		var url = $(this).data("url");
		var id = $(this).parent().parent().parent().find('.cardID').text();
		
		console.log("url: " + url + ", id: " + id);
		
		$.ajax({
			
	        type: 'POST',
	        url: url,
	        data: {"id": id},
	        dataType: 'json'
		
	    }).done (function(data) {
	    	
	    	console.log(data);
	    	var alertHtml = "";
	    	alertHtml += "<div class=\"alert alert-success\">\n";
	    	alertHtml += "<a href=\"#\" class=\"close\" data-dismiss=\"alert\" aria-label=\"close\">&times;</a>\n";
	    	alertHtml += "<span id=\"alert-message\">" + data.message + "</span>\n"	
	    		
	    	$('#alert-box').html(alertHtml);
	    
	    	$("html, body").animate({ scrollTop: 0 }, "slow");
	    	
	    }).fail (function(err) {
			
	    	console.error(err)
			
	    });
		
	});
	
	
	function validateCreditCard(ref) {
		var validated = true;
		$(ref).parent().parent().parent().find('.form-input').each(function(i, obj) {
			
			var val = $(obj).val();
			if (val == "") {
				var errorsHtml = $(obj).parent().find('.error').html();
				
				errorsHtml += "Field is required" + "</br>";
				
				$(obj).parent().find('.error').html(errorsHtml);
				validated = false;
			}
			
		});
		
		var cvv = $(ref).parent().parent().parent().find('[name="cvv"]').val();
		
		if(!cvv.match(/^\d+$/)) {
			var errorsHtml = $(ref).parent().parent().parent().find('.cvv-error').html();
			errorsHtml += "CVV should contain only digits" + "</br>";
			$(ref).parent().parent().parent().find('.cvv-error').html(errorsHtml);
			validated = false;
		}
		return validated;
	}
	
	//remove error messages
	function clearErrors() {
		
		$('.error').each(function(i, obj) {
			$(obj).html("");
		});
	}
	
	function processErrors(err) { 
		for (var i = 0; i < err.responseJSON.length; i++) {
			var error = err.responseJSON[i];
			var field = error.field;
			var errorMessage = error.error;
			
			$('.form-input').each(function(i, obj) {
				
				if (field == $(obj).attr('name')) {
					var errorsHtml = $(obj).parent().find('.error').html();
					
					errorsHtml += errorMessage + "</br>";
					
					$(obj).parent().find('.error').html(errorsHtml);
					
				}
			});
			
		}
	}
	
	
	
});