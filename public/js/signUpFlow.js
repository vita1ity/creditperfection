$(document).ready(function() {
	//register new user
	$(document).on('click', '#register', function (e) {
		
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
		
		console.log(url, ", " + firstName + ", " + lastName + ", " + email + ", " + address + ", " +
				city + ", " + state + ", " + zip + ", " + password);
		
		var userJSON =  {firstName: firstName, lastName: lastName, email: email, address: address,
				city: city, state: state, zip: zip, password: password};
		
		$.ajax({
			
	        type: 'POST',
	        url: url, 
	        contentType: 'application/json',
	        data: JSON.stringify(userJSON),
	        dataType: 'json'
		
	    }).done (function(data) {
	    	
	    	$('#register').addClass('hidden');
	    	$('.choose-product-form').removeClass('hidden');
	    
	    }).fail (function(err) {
			
	    	console.error(err)
			
			for (var i = 0; i < err.responseJSON.length; i++) {
				var error = err.responseJSON[i];
				var field = error.field;
				var errorMessage = error.error;
				
				if (field == "email") {
					var errors = $('#email-error').html();
					errors += errorMessage + "</br>";
					$('#email-error').html(errors);
					
				}
				//TODO other fields
			}
	    });
	});
	
	//choose product
	$(document).on('click', '#chooseProduct', function (e) {
		
		var url = $(this).data("url");
		var product = $('[name="product"]').val();
		
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
	$(document).on('click', '#processPayment', function (e) {
		
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
	        dataType: 'text'
		
	    }).done (function(data) {
	    	
	    	console.log("Payment processed successfully");
	    
	    }).fail (function(err) {
			console.error(err)
	    });
	});
	
	//remove error messages
	function clearErrors() {
		
		$('.error').each(function(i, obj) {
			$(obj).html("");
		});
	}
	
});
