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
			
	    	console.error(err)
			processErrors(err);
			
	    });
		
	});
	
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