$(document).ready(function() {
	
	$(document).on('click', '#updateAndProcess', function (e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data("url");
		var reportUrl = $(this).data("report-url");
		
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
	    	
	    	setTimeout(function() {
	    		window.location.href = reportUrl;
	    	}, 5000);
	    	
	    	
	    }).fail (function(err) {
			
	    	console.error(err);
	    	
	    	if (err.responseJSON.message) {
	    		showErrorAlert(err.responseJSON.message);
	    		$("html, body").animate({ scrollTop: 0 }, "slow");
	    	}
	    	else {
	    		processErrors(err, form);
	    	}
	    });
	});
	
	
	
});