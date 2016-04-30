$(document).ready(function() {
	//register new user
	$(document).on('submit', '.register-form', function (e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data("url");
		var firstName = $('[name="firstName"]').val();
		var lastName = $('[name="lastName"]').val();
		var email = $('[name="email"]').val();
		var address = $('[name="address"]').val();
		var city = $('[name="city"]').val();
		var state = $('[name="state"]').val();
		var zip = $('[name="zip"]').val();
		var password = $('[name="password"]').val();
		
		var userJSON =  {firstName: firstName, lastName: lastName, email: email, address: address,
				city: city, state: state, zip: zip, password: password};
		
		$.ajax({
			
	        type: 'POST',
	        url: url, 
	        contentType: 'application/json',
	        data: JSON.stringify(userJSON),
	        dataType: 'json'
		
	    }).done (function(data) {
	    	
	    	console.log(data);
	    	$('#register').addClass('hidden');
	    	$('.choose-product-form').removeClass('hidden');
	    
	    }).fail (function(err) {
			
	    	//console.error(err)
			processErrors(err);
			
	    });
	});
	
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
	
	//choose product
	$(document).on('submit', '.choose-product-form', function (e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data("url");
		var product = $('[name="product"]').val();
		
		if (product == "") {
			var errorsHtml = $(this).parent().find('.error').html();
			
			errorsHtml += "Please choose the product" + "</br>";
			
			$(this).parent().find('.error').html(errorsHtml);
			return;
		}
		
		$.ajax({
			
	        type: "POST",
	        url: url,
	        data: {"product": product},
	        dataType: 'text'
		
	    }).done (function(data) {
	    	
	    	$('#chooseProduct').addClass('hidden');
	    	$('.payment-form').removeClass('hidden');
	    
	    }).fail (function(err) {
			console.error(err)
	    });
	});
	
	//choose product
	$(document).on('submit', '.payment-form', function (e) {
		
		e.preventDefault();
		
		clearErrors();
		$('#transactionReportError').text('');
		 
		if (!validateCreditCard()) {
			return;
		}
		
		var url = $(this).data("url");
		
		var name = $('[name="name"]').val();
		var cardType = $('[name="cardType"]').val();
		var digits = $('[name="digits"]').val();
		var month = $('[name="month"]').val();
		var year = $('[name="year"]').val();
		var cvv = $('[name="cvv"]').val();
		
		var creditCardJSON =  {name: name, cardType: cardType, digits: digits, month: month,
				year: year, cvv: cvv};
		
		$.ajax({
			
	        type: 'POST',
	        url: url,
	        contentType: 'application/json',
	        data: JSON.stringify(creditCardJSON),
	        dataType: 'json'
		
	    }).done (function(data) {
	    	
	    	console.log(data);
	    	
	    	var url = data.reportUrl;
	    	window.location.href = url;
	    	
	    
	    }).fail (function(err) {
			console.error(err)
			
			//transaction or report error
			$('#transactionReportError').text(err.responseJSON.errorMessage);
			
			//validation errors
			if (err.responseJSON.length != 1) {
				processErrors(err);
			}
			
			
	    });
	});
	
	//remove error messages
	function clearErrors() {
		
		$('.error').each(function(i, obj) {
			$(obj).html("");
		});
	}
	
	function validateCreditCard() {
		var validated = true;
		$('.form-input').each(function(i, obj) {
			
			var val = $(obj).val();
			if (val == "") {
				var errorsHtml = $(obj).parent().find('.error').html();
				
				errorsHtml += "Field is required" + "</br>";
				
				$(obj).parent().find('.error').html(errorsHtml);
				validated = false;
			}
			
		});
		
		var cvv = $('[name="cvv"]').val();
		
		if(!cvv.match(/^\d+$/)) {
			var errorsHtml = $('#cvv-error').html();
			errorsHtml += "CVV should contain only digits" + "</br>";
			$('#cvv-error').html(errorsHtml);
			validated = false;
		}
		return validated;
	}
	
});
