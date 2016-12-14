$(document).ready(function() {
	
	$(document).on('click', '#updateCreditCard', function (e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data("url");
		
		var id = $(this).closest('.update-form').find('.cardID').val();
		var name = $(this).closest('.update-form').find('[name="name"]').val();
		var cardType = $(this).closest('.update-form').find('[name="cardType"]').val();
		var digits = $(this).closest('.update-form').find('[name="digits"]').val();
		var month = $(this).closest('.update-form').find('[name="month"]').val();
		var year = $(this).closest('.update-form').find('[name="year"]').val();
		var cvv = $(this).closest('.update-form').find('[name="cvv"]').val();
		
		var form = $(this).closest('.update-form');
		
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