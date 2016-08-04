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

function showErrorAlert(message) {
	var alertHtml = "";
	alertHtml += "<div class=\"alert alert-danger\">\n";
	alertHtml += "<a href=\"#\" class=\"close\" data-dismiss=\"alert\" aria-label=\"close\">&times;</a>\n";
	alertHtml += "<span id=\"alert-message\">" + message + "</span>\n"	
		
	$('#alert-box').html(alertHtml);
}