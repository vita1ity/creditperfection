$(document).ready(function() {
	
	//register new user
	$(document).on('submit', '.register-form', function (e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data("url");
		var firstName = $(this).find('[name="firstName"]').val();
		var lastName = $(this).find('[name="lastName"]').val();
		var email = $(this).find('[name="email"]').val();
		var password = $(this).find('[name="password"]').val();
		var confirmEmail = $(this).find('[name="confirmEmail"]').val();
		var confirmPassword = $(this).find('[name="confirmPassword"]').val();
		var address = $(this).find('[name="address"]').val();
		var city = $(this).find('[name="city"]').val();
		var state = $(this).find('[name="state"]').val();
		var zip = $(this).find('[name="zip"]').val();
		
		
		var userJSON =  {firstName: firstName, lastName: lastName, email: email, password: password, confirmEmail: confirmEmail,
				confirmPassword: confirmPassword, address: address, city: city, state: state, zip: zip};
		console.log(userJSON);
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
		
		console.log("process payment");
		
		clearErrors();
		$('#transactionReportError').text('');
		 
		if (!validateCreditCard(this)) {
			console.log("validation failed");
			return;
		}
		
		var url = $(this).data('url');
		
		var name = $('[name="name"]').val();
		var cardType = $('[name="cardType"]').val();
		var digits = $('[name="digits"]').val();
		var month = $('[name="month"]').val();
		var year = $('[name="year"]').val();
		var cvv = $('[name="cvv"]').val();
		
		var reportPageUrl = $(this).data('report-page-url');
		
		var creditCardJSON =  {name: name, cardType: cardType, digits: digits, month: month,
				year: year, cvv: cvv};
		
		console.log(creditCardJSON);
		
		$.ajax({
			
	        type: 'POST',
	        url: url,
	        contentType: 'application/json',
	        data: JSON.stringify(creditCardJSON),
	        dataType: 'json'
		
	    }).done (function(data) {
	    	
	    	console.log(data);
	    	
	    	var url = data.reportUrl;
	    	
	    	$.redirect(reportPageUrl, {'url': url});
	    	
	    
	    }).fail (function(err) {
			console.error(err)
			
			var errorResponse = err.responseJSON;
			if (errorResponse.errors) {
				var errorText = '';
				for (var i = 0; i < errorResponse.errors.length; i++) {
					errorText += errorResponse.errors[i].errorMessage + '</br>';
				}
				$('#transactionReportError').html(errorText);
					
			}
			else {
				//transaction or report error
				$('#transactionReportError').text(err.responseJSON.errorMessage);
			}
			
			//validation errors
			processErrors(err);
			
	    });
	});
	
	//remove error messages
	function clearErrors() {
		
		$('.error').each(function(i, obj) {
			$(obj).html("");
		});
	}
	
	function validateCreditCard(ref) {
		var validated = true;
		$(ref).find('.form-input').each(function(i, obj) {
			
			var val = $(obj).val();
			if (val == "") {
				var errorsHtml = $(obj).parent().find('.error').html();
				
				errorsHtml += "Field is required" + "</br>";
				
				$(obj).parent().find('.error').html(errorsHtml);
				validated = false;
			}
			
		});
		
		var cvv = $(ref).parent().find('[name="cvv"]').val();
		
		if(!cvv.match(/^\d+$/)) {
			var errorsHtml = $(ref).parent().find('.cvv-error').html();
			errorsHtml += "CVV should contain only digits" + "</br>";
			$(ref).parent().find('.cvv-error').html(errorsHtml);
			validated = false;
		}
		return validated;
	}
	
});
