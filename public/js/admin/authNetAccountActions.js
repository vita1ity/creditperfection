var deleteAuthNetAccountId = '';
var authNetAccountPanel = new Object();

$(document).ready(function() {
	
	$(document).on('click', '#confirmAddAuthNetAccount', function(e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data('url');
		
		var name = $(this).closest('.modal-content').find('.name').val();
		var description = $(this).closest('.modal-content').find('.description').val();
		var loginId = $(this).closest('.modal-content').find('.login-id').val();
		var transactionKey = $(this).closest('.modal-content').find('.transaction-key').val();
		var isEnabled = false;
		if ($(this).closest('.modal-content').find('.is-enabled').is(':checked')) {
			isEnabled = true;
		}
		var priority = $(this).closest('.modal-content').find('.priority').val();
		
		
		var accountJSON = {name: name, description: description, loginId: loginId, transactionKey: transactionKey, isEnabled: isEnabled, priority: priority};
		
		var form = $(this).closest('.modal-content');
		
		$.ajax ({
			type: 'POST',
	        url: url, 
	        contentType: 'application/json',
	        data: JSON.stringify(accountJSON),
	        dataType: 'json'	
		}).done (function(data) {
			
			console.log(data);
			
			$('#addAuthNetAccount').modal('toggle');
			
			showSuccessAlert(data.message);
	    
	    	$('#add-user').modal('toggle');
	    	
	    	//display new merchant account
	    	var id = data.id;
	    	
	    	var accountHtml = $('.panel').html();
	    	accountHtml = '<div class="panel panel-default">\n' + accountHtml + "\n</div>";
	    	
	    	var index = parseInt($('.index:last').text()) + 1;
	    	
	    	if (isNaN(index)) {
	    		
	    		index = 1;
	    	}
	    	
	    	$('#accordion').append(accountHtml);
	    	
	    	$('.panel-title-text:last').prop('href', '#collapse' + (index - 1));
	    	$('.collapse:last').prop('id', 'collapse' + (index - 1));
	    	$('.panel-title-text:last').html("<span class=\"index\">" + 
	    			index + "</span>. " + name);
	    	$('.authNetAccountId:last').text(id);
	    	
	    	$('.name:last').val(name);
	    	
	    	$('.description:last').val(description);
	    	$('.login-id:last').val(loginId);
	    	$('.transaction-key:last').val(transactionKey);
	    	$('.is-enabled:last').val(isEnabled);
	    	$('.priority:last').val(priority);
			
		}).fail (function(err) {
			console.error(err);
			processErrors(err, form);
		});
		
	});
	
	$(document).on('click', '#editAuthNetAccount', function(e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data('url');
		
		var id = $(this).closest('.edit-form').find('.authNetAccountId').text();
		var name = $(this).closest('.edit-form').find('.name').val();
		var description = $(this).closest('.edit-form').find('.description').val();
		var loginId = $(this).closest('.edit-form').find('.login-id').val();
		var transactionKey = $(this).closest('.edit-form').find('.transaction-key').val();
		var isEnabled = false;
		if ($(this).closest('.edit-form').find('.is-enabled').is(':checked')) {
			isEnabled = true;
		}
		var priority = $(this).closest('.edit-form').find('.priority').val();
		
		console.log("isEnabled:" + isEnabled);
		var accountJSON = {id: id, name: name, description: description, loginId: loginId, transactionKey: transactionKey, isEnabled: isEnabled, priority: priority};
		
		var form = $(this).closest('.edit-form');
		
		$.ajax({
			type: 'POST',
			url: url,
			contentType: 'application/json',
			data: JSON.stringify(accountJSON),
			dataType: 'json'
		}).done (function(data) {
			
			showSuccessAlert(data.message);
		
	    	$("html, body").animate({ scrollTop: 0 }, "slow");
			
		}).fail (function(err) {
			console.error(err);
			processErrors(err, form);
		});
		
		
	});
	
	$('#deleteAuthNetAccount').on('show.bs.modal', function (e) {
	    var trigger = $(e.relatedTarget);
	    var id = $(trigger).closest('.edit-form').find('.authNetAccountId').text();
	    
	    deleteAuthNetAccountId = id;
	    authNetAccountPanel = $(trigger).closest('.panel');
	});
	
	$(document).on('click', '#confirmDeleteAuthNetAccount', function(e) {
		
		e.preventDefault();
		
		var url = $(this).data('url');
		
		var id = deleteAuthNetAccountId;
		var deletePanel = authNetAccountPanel;
		
		$.ajax({
			type: 'POST',
			url: url,
			data: {id: id},
			dataType: 'json'
		}).done (function(data) {
			
			showSuccessAlert(data.message);
			
			$(deletePanel).remove();
	    	
	    	$('#deleteAuthNetAccount').modal('toggle');
	    	
	    	$("html, body").animate({ scrollTop: 0 }, "slow");
			
		}).fail (function(err) {
			console.error(err);
		});
		
	});
	
});