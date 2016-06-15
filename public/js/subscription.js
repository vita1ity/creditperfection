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
		
		var url = $(this).data("url");
		
		$.ajax({
			
	        type: "POST",
	        url: url,
	        dataType: 'json'
		
	    }).done (function(data) {
	    	
	    	showSuccessAlert(data.message);
	    	$('#cancelSubscription').modal('toggle');
	    
	    }).fail (function(err) {
	    	console.log(err);
	    	showErrorAlert(err.responseJSON.errorMessage);
	    	$('#cancelSubscription').modal('toggle');
	    });
	});
	
});

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