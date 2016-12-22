window.onload = function(){
	
	if ($('#isLogin').text() == 'true') {
		$('#login').modal('toggle');
	}
}

$(document).ready(function() {
	
	$('[role="toggle_forgot_password_form"]').on('click', function (e) {
		e.preventDefault();
	    $(this).parents('.login-form-block').find('.flipper').toggleClass('rotate');
	});


	
	
	$(document).on('click', '#sendLogin', function(e) {
		
		e.preventDefault();
		
		var url = $(this).data("url");
		var email = $(this).closest('.login-form').find('[name="email"]').val();
		var password = $(this).closest('.login-form').find('[name="password"]').val();
		
		var adminUrl = $(this).data("admin-url");
		var userUrl = $(this).data("user-url");
		var paymentUrl = $(this).data("payment-url");
		
		$.ajax({
			
	        type: 'POST',
	        url: url,
	        data: {"email": email, "password": password},
	        dataType: 'json'
		
	    }).done (function(data) {
	    	
	    	console.log(data);
	    	
	    	var role = data.role;
	    	if (role == "admin") {
	    		
	    		window.location.href = adminUrl;
	    	}
	    	else {
	    		if (data.subscription == false) {
	    			window.location.href = paymentUrl;
	    		}
	    		else {
	    			window.location.href = userUrl;
	    		}
	    		
	    	}
	    	
	    
	    }).fail (function(err) {
			
	    	console.error(err);
	    	
	    	var errorMessage = err.responseJSON.errorMessage;
	    	
	    	var alertHtml = "";
	    	alertHtml += "<div class=\"alert alert-small alert-danger\">\n";
	    	alertHtml += "<a href=\"#\" class=\"close\" data-dismiss=\"alert\" aria-label=\"close\">&times;</a>\n";
	    	alertHtml += "<span id=\"alert-message\">" + errorMessage + "</span>\n"	
	    		
	    	$('#login-alert-box').html(alertHtml);
	    	
	    });
		
	});
	
	
	$('.login-modal').keypress(function (e) {
		  if (e.which == 13) {
			  
			  if ($('.flipper').hasClass('rotate')) {
				  $('#sendPassword').click();
			  }
			  else {
				  $('#sendLogin').click();  
			  }
			  
			  return false;  
		  }
	});
	
	
	
	$(document).on('click', '#sendPassword', function(e) {
		
		e.preventDefault();
		
		var url = $(this).data("url");
		var email = $(this).closest('.forgot-pass').find('[name="email"]').val();
		
		$.ajax({
			
	        type: 'POST',
	        url: url,
	        data: {"email": email},
	        dataType: 'json'
		
	    }).done (function(data) {
	    	
	    	console.log(data);
	    	
	    	var message = data.message;
	    	var alertHtml = "";
	    	alertHtml += "<div class=\"alert alert-small alert-success\">\n";
	    	alertHtml += "<a href=\"#\" class=\"close\" data-dismiss=\"alert\" aria-label=\"close\">&times;</a>\n";
	    	alertHtml += "<span id=\"alert-message\">" + message + "</span>\n"	
	    		
	    	$('#forgot-pass-alert-box').html(alertHtml);
	    	
	    
	    }).fail (function(err) {
			
	    	console.error(err);
	    	
	    	var errorMessage = err.responseJSON.errorMessage;
	    	
	    	var alertHtml = "";
	    	alertHtml += "<div class=\"alert alert-small alert-danger\">\n";
	    	alertHtml += "<a href=\"#\" class=\"close\" data-dismiss=\"alert\" aria-label=\"close\">&times;</a>\n";
	    	alertHtml += "<span id=\"alert-message\">" + errorMessage + "</span>\n"	
	    		
	    	$('#forgot-pass-alert-box').html(alertHtml);
	    	
	    });
		
	});
});