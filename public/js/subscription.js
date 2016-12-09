$(document).ready(function() {

	$(document).on('click', '#upgradeSubscription', function (e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data("url");
		var product = $(this).closest('.update-subscription-form').find('[name="product"]').val();
		var productText = $(this).closest('.update-subscription-form').find('[name="product"] option:selected').text();
		
		if (product == "") {
			var errorsHtml = $('#product-error').html();
			
			errorsHtml += "Please choose the product" + "</br>";
			
			$('#product-error').html(errorsHtml);
			return;
		}
		
		$.ajax({
			
	        type: "POST",
	        url: url,
	        data: {"product": product},
	        dataType: 'json'
		
	    }).done (function(data) {
	    	
	    	showSuccessAlert(data.message);
	    	$('.product').text(productText);
	    	
	    
	    }).fail (function(err) {
			console.error(err)
	    });
	});
	
	
	$(document).on('click', '#confirmCancelSubscription', function (e) {
		
		e.preventDefault();
		
		var url = $(this).data('url');
		var cancelUrl = $(this).data('cancel-url');
		
		
		$.ajax({
			
	        type: "GET",
	        url: url,
	        dataType: 'json'
		
	    }).done (function(data) {
	    	
	    	if (!data.statement) {
	    		//propose discount to user
	    		$('#cancelSubscription').modal('toggle');
	    		$('#freeWeek').modal('toggle');
	    	}
	    	else {
	    		
	    		sendCancelRequest($('#cancelSubscription'), cancelUrl);
	    	}
	    	
	    
	    }).fail (function(err) {
	    	
	    	console.log(err);
	    	showErrorAlert(err.responseJSON.errorMessage);
	    	$('#cancelSubscription').modal('toggle');
	    	
	    });
		
	});
	
	$(document).on('click', '#freeWeekCancel', function (e) {
		
		$('#freeWeek').modal('toggle');
		$('#freeMonth').modal('toggle');
	});
	
	$(document).on('click', '#freeMonthCancel', function (e) {
		
		$('#freeMonth').modal('toggle');
		$('#yearDiscount').modal('toggle');
	});
	
	$(document).on('click', '#yearDiscountCancel', function (e) {
		
		e.preventDefault();
		
		var url = $(this).data("url");
		
		sendCancelRequest($('#yearDiscount'), url);
		
		
	});
	
	$(document).on('click', '#acceptFreeWeekDiscount', function (e) {
		
		e.preventDefault();
		
		var url = $(this).data('url');
		
		$.ajax({
			
	        type: 'POST',
	        url: url,
	        dataType: 'json'
		
	    }).done (function(data) {
	    	
	    	$('#freeWeek').modal('toggle');
	    	showSuccessAlert(data.message);
	    	
	    
	    }).fail (function(err) {
	    	
	    	console.log(err);
	    	showErrorAlert(err.responseJSON.errorMessage);
	    	$('#freeWeek').modal('toggle');
	    	
	    });
		
	});
	
	$(document).on('click', '#acceptFreeMonthDiscount', function (e) {
		
		e.preventDefault();
		
		var url = $(this).data('url');
		
		$.ajax({
			
	        type: 'POST',
	        url: url,
	        dataType: 'json'
		
	    }).done (function(data) {
	    	
	    	$('#freeMonth').modal('toggle');
	    	showSuccessAlert(data.message);
	    	
	    
	    }).fail (function(err) {
	    	
	    	console.log(err);
	    	showErrorAlert(err.responseJSON.errorMessage);
	    	$('#freeMonth').modal('toggle');
	    	
	    });
		
	});
	
	$(document).on('click', '#acceptYearDiscount', function (e) {
		
		e.preventDefault();
		
		var url = $(this).data('url');
		
		$.ajax({
			
	        type: 'POST',
	        url: url,
	        dataType: 'json'
		
	    }).done (function(data) {
	    	
	    	$('#yearDiscount').modal('toggle');
	    	showSuccessAlert(data.message);
	    	
	    
	    }).fail (function(err) {
	    	
	    	console.log(err);
	    	showErrorAlert(err.responseJSON.errorMessage);
	    	$('#yearDiscount').modal('toggle');
	    	
	    });
		
	});
	
	
});




function sendCancelRequest(modal, url) {
	$.ajax({
		
        type: "POST",
        url: url,
        dataType: 'json'
	
    }).done (function(data) {
    	
    	showSuccessAlert(data.message);
    	$(modal).modal('toggle');
    
    }).fail (function(err) {
    	console.log(err);
    	showErrorAlert(err.responseJSON.errorMessage);
    	$(modal).modal('toggle');
    });
}

function clearErrors() {
	
	$('.error').each(function(i, obj) {
		$(obj).html("");
	});
}

function showSuccessAlert(message) {
	var alertHtml = "";
	alertHtml += "<div class=\"alert alert-success\">\n";
	alertHtml += "<a href=\"#\" class=\"close\" data-dismiss=\"alert\" aria-label=\"close\">&times;</a>\n";
	alertHtml += "<span id=\"alert-message\">" + message + "</span>\n"	
		
	$('#alert-box').html(alertHtml);
}

function showErrorAlert(message) {
	var alertHtml = "";
	alertHtml += "<div class=\"alert alert-danger\">\n";
	alertHtml += "<a href=\"#\" class=\"close\" data-dismiss=\"alert\" aria-label=\"close\">&times;</a>\n";
	alertHtml += "<span id=\"alert-message\">" + message + "</span>\n"	
		
	$('#alert-box').html(alertHtml);
}