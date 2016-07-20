var deleteCreditCardId = '';
var creditCardPanel = new Object();

$(document).ready(function() {
	
	$(document).on('click', '#confirmAddCreditCard', function (e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data("url");
		var name = $(this).closest('.modal-content').find('[name="name"]').val();
		var cardType = $(this).closest('.modal-content').find('[name="cardType"]').val();
		var digits = $(this).closest('.modal-content').find('[name="digits"]').val();
		var month = $(this).closest('.modal-content').find('[name="month"]').val();
		var year = $(this).closest('.modal-content').find('[name="year"]').val();
		var cvv = $(this).closest('.modal-content').find('[name="cvv"]').val();
		var ownerId = $(this).closest('.modal-content').find('[name="owner"]').val();
		
		var form = $(this).closest('.modal-content');
		
		var creditCardJSON =  {name: name, cardType: cardType, digits: digits, month: month,
				year: year, cvv: cvv, ownerId: ownerId};
		
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
	    
	    	$('#add-credit-card').modal('toggle');
	    	
	    	//display new credit card
	    	var id = data.id;
	    	
	    	var creditCardHtml = $('.panel').html();
	    	creditCardHtml = '<div class="panel panel-default">\n' + creditCardHtml + "\n</div>";
	    	
	    	var index = parseInt($('.index:last').text()) + 1;
	    	
	    	$('#accordion').append(creditCardHtml);
	    	
	    	console.log("index: " + index);
	    	$('.panel-title-text:last').prop('href', '#collapse' + (index - 1));
	    	$('.collapse:last').prop('id', 'collapse' + (index - 1));
	    	$('.panel-title-text:last').html("<span class=\"index\">" + 
	    			index + "</span>. " + name + " - " + digits);
	    	$('.cardID:last').text(id);
	    	
	    	$('.name').eq(-1).val(name);
	    	
	    	$('.card-type:last > option').each(function(i, obj) {
	    		
	    		if (cardType == $(obj).val()) {
	    			$(obj).prop("selected", true);
	    		}
	    		else {
	    			$(obj).prop("selected", false);
	    		}
	    	});
	    	
	    	$('.digits').eq(-1).val(digits);
	    	$('.cvv').eq(-1).val(cvv);
	    	
	    	$('.month:last > option').each(function(i, obj) {
	    		
	    		
	    		if (month == $(obj).val()) {
	    			$(obj).prop("selected", true);
	    		}
	    		else {
	    			$(obj).prop("selected", false);
	    		}
	    	});
	    	$('.year:last > option').each(function(i, obj) {
	    		
	    		if (year == $(obj).val()) {
	    			$(obj).prop("selected", true);
	    		}
	    		else {
	    			$(obj).prop("selected", false);
	    		}
	    	});
	    	
	    	
	    }).fail (function(err) {
			
	    	console.error(err);
			processErrors(err, form);
			
	    });
	});
	
	$(document).on('click', '#editCreditCard', function (e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data("url");
		
		var id = $(this).closest('.edit-form').find('.cardID').text();
		var name = $(this).closest('.edit-form').find('[name="name"]').val();
		var cardType = $(this).closest('.edit-form').find('[name="cardType"]').val();
		var digits = $(this).closest('.edit-form').find('[name="digits"]').val();
		var month = $(this).closest('.edit-form').find('[name="month"]').val();
		var year = $(this).closest('.edit-form').find('[name="year"]').val();
		var cvv = $(this).closest('.edit-form').find('[name="cvv"]').val();
		
		var form = $(this).closest('.edit-form');
		
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
	
	$('#deleteCreditCard').on('show.bs.modal', function (e) {
	    var trigger = $(e.relatedTarget);
	    var id = $(trigger).closest('.edit-form').find('.cardID').text();
	    
	    deleteCreditCardId = id;
	    creditCardPanel = $(trigger).closest('.panel');
	});
	
	$(document).on('click', '#confirmDeleteCreditCard', function (e) {
		
		e.preventDefault();
		
		var url = $(this).data("url");
		
		var id = deleteCreditCardId;
		var panel = creditCardPanel;
		
		console.log("url: " + url + ", id: " + id);
		
		$.ajax({
			
	        type: 'POST',
	        url: url,
	        data: {"id": id},
	        dataType: 'json'
		
	    }).done (function(data) {
	    	
	    	console.log(data);

	    	showSuccessAlert(data.message);
	
	    	$(panel).remove();
	    	$('#deleteCreditCard').modal('toggle');
	    	
	    	$("html, body").animate({ scrollTop: 0 }, "slow");
	    	
	    	
	    
	    	
	    }).fail (function(err) {
			
	    	console.error(err)
			
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
	
$(document).on('change', '.user-cards', function (e) {
		
		e.preventDefault();
		
		var url = $(this).data('url');
		var userId = $(this).find(":selected").val();
		
		console.log("url: " + url + "; userId: " + userId);
		
		var userHtml = $(this);
		
		$.ajax({
			
			type: 'GET',
	        url: url,
	        data: {'userId': userId},
	        dataType: 'json'
			
		}).done (function(data) {
			
			console.log(data);
			
			var optionsHtml = '<option value=\"\">Select Card Owner</option>\n';
			for (var i = 0; i < data.length; i++) {
				var creditCard = data[i];
				optionsHtml += "<option value=\"" + creditCard.id + "\"> " + (i + 1) + ". " + creditCard.cardType + 
					": " + creditCard.digits + " </option> \n";
				
			}
			
			$(userHtml).closest('.modal-body').find('.credit-card').html(optionsHtml);
			$(userHtml).closest('.modal-body').find('.credit-card').prop("disabled", false);
			
		}).fail (function(err) {
			
			console.error(err);
			
		});
		
	});
	
});