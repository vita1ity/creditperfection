var deleteUserId = '';
var userPanel = new Object();

$(document).ready(function() {
	
	$(document).on('click', '#confirmAdd', function (e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data("url");
		var firstName = $(this).closest('.modal-content').find('[name="firstName"]').val();
		var lastName = $(this).closest('.modal-content').find('[name="lastName"]').val();
		var email = $(this).closest('.modal-content').find('[name="email"]').val();
		var address = $(this).closest('.modal-content').find('[name="address"]').val();
		var city = $(this).closest('.modal-content').find('[name="city"]').val();
		var state = $(this).closest('.modal-content').find('[name="state"]').val();
		var zip = $(this).closest('.modal-content').find('[name="zip"]').val();
		var password = $(this).closest('.modal-content').find('[name="password"]').val();
		
		var form = $(this).closest('.modal-content');
		
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
	    	
	    	showSuccessAlert(data.message);
	    
	    	$('#add-user').modal('toggle');
	    	
	    	//display new user
	    	var id = data.id;
	    	
	    	var userHtml = $('.panel').html();
	    	userHtml = '<div class="panel panel-default">\n' + userHtml + "\n</div>";
	    	
	    	var index = parseInt($('.index:last').text()) + 1;
	    	
	    	$('#accordion').append(userHtml);
	    	
	    	console.log("index: " + index);
	    	$('.panel-title-text:last').prop('href', '#collapse' + (index - 1));
	    	$('.collapse:last').prop('id', 'collapse' + (index - 1));
	    	$('.panel-title-text:last').html("<span class=\"index\">" + 
	    			index + "</span>. " + firstName + " " + lastName);
	    	$('.userID:last').text(id);
	    	
	    	$('.first-name:last').val(firstName);
	    	
	    	$('.last-name:last').val(lastName);
	    	$('.email:last').val(email);
	    	$('.address:last').val(address);
	    	$('.city:last').val(city);
	    	$('.state:last > option').each(function(i, obj) {
	    		if (state == $(obj).val()) {
	    			$(obj).prop("selected", true);
	    		}
	    		else {
	    			$(obj).prop("selected", false);
	    		}
	    	});
	    	$('.zip:last').val(zip);
	    	$('.active:last').prop("checked", false);
	    	
	    	
	    	
	    }).fail (function(err) {
			
	    	console.error(err);
			processErrors(err, form);
			
	    });
	});
	
	$(document).on('click', '#editUser', function (e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data("url");
		var id = $(this).closest('.edit-form').find('.userID').text();
		var firstName = $(this).closest('.edit-form').find('[name="firstName"]').val();
		var lastName = $(this).closest('.edit-form').find('[name="lastName"]').val();
		var email = $(this).closest('.edit-form').find('[name="email"]').val();
		var address = $(this).closest('.edit-form').find('[name="address"]').val();
		var city = $(this).closest('.edit-form').find('[name="city"]').val();
		var state = $(this).closest('.edit-form').find('[name="state"]').val();
		var zip = $(this).closest('.edit-form').find('[name="zip"]').val();
		var password = $(this).closest('.edit-form').find('[name="password"]').val();
		
		var form = $(this).closest('.edit-form');
		
		var active = false;
		if ($(this).closest('.edit-form').find('[name="active"]').is(':checked')) {
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

	    	showSuccessAlert(data.message);
	    
	    	$("html, body").animate({ scrollTop: 0 }, "slow");
	    	
	    }).fail (function(err) {
			
	    	console.error(err);
			processErrors(err, form);
			
	    });
	});
	
	$('#deleteUser').on('show.bs.modal', function (e) {
	    var trigger = $(e.relatedTarget);
	    var id = $(trigger).closest('.edit-form').find('.userID').text();
	    
	    deleteUserId = id;
	    userPanel = $(trigger).closest('.panel');
	});
	
	$(document).on('click', '#confirmDeleteUser', function (e) {
		
		e.preventDefault();
		
		var url = $(this).data("url");
		
		var id = deleteUserId;
		var panel = userPanel;
		
		console.log("url: " + url + ", id: " + id);
		
		$.ajax({
			
	        type: 'POST',
	        url: url,
	        data: {"id": id},
	        dataType: 'json'
		
	    }).done (function(data) {
	    	
	    	console.log(data);
	    	
	    	showSuccessAlert(data.message);
	    	
	    	$("html, body").animate({ scrollTop: 0 }, "slow");
	    	
	    	$(panel).remove();
	    	
	    	$('#deleteUser').modal('toggle');
	    
	    }).fail (function(err) {
			
	    	console.error(err);
			
	    });
		
	});
	
	$(document).on('click', '.pager_item', function (e) {
		e.preventDefault();
		var url = $(this).data("page");
		console.log(url);
		
		$.ajax({
	        url:url,
	        type: "GET",
	        dataType: 'json'
	        	
		}).done (function(data) {
			
			console.log(data);
			
			
			//show users for the selected page
			$('.panel').each(function(i, obj) {
				
				var user = data[i];
				if (user == null) {
					$(obj).hide();
				}
				else {
					$(obj).show();
					
					var firstName = user.firstName;
					var lastName = user.lastName;
					var id = user.id;
					var email = user.email;
					var address = user.address;
					var city = user.city;
					var state = user.state;
					var zip = user.zip;
					var active = user.active;
					
					$(obj).find('.name').text(firstName + ' ' + lastName);
					$(obj).find('.userID').text(id);
					$(obj).find('.first-name').val(firstName);
					$(obj).find('.last-name').val(lastName);
					$(obj).find('.email').val(email);
					$(obj).find('.address').val(address);
					$(obj).find('.city').val(city);
					
					$(obj).find('.state > option').each(function(j, optObj) {
			    		if (state == $(optObj).val()) {
			    			$(optObj).prop("selected", true);
			    		}
			    		else {
			    			$(optObj).prop("selected", false);
			    		}
			    	});
					
					$(obj).find('.zip').val(zip);
					if (active) {
						$(obj).find('.active').prop("checked", true);
					}
					else {
						$(obj).find('.active').prop("checked", false);
					}
				}
		    	
			});
			
			//update pagination
			console.log("url: " + url);
			
			var res = url.split("=");
			urlBase = res[0] + "=";
			var page = parseInt(res[1]);
			
			console.log(page);
			
			$('#currentPage').val(page);
			var startDisplayPage = parseInt($('#startDisplayPage').val());
			console.log("startDisplayPage: " + startDisplayPage);
			//console.log(startDisplayPage / 10);
			
			if ((Math.floor((page - 1) / 10))  != (Math.floor(startDisplayPage / 10))) {
				
				startDisplayPage = Math.floor((page - 1) / 10) * 10 + 1;
				$('#startDisplayPage').val(startDisplayPage);
				console.log("start display page changed: " + startDisplayPage);
			}
			
			$('#pageUrl').val(url);
			
			console.log("Page url: " + $('#pageUrl').val());
			
			var pager = "";
			
			
			console.log(page);
			var numberOfPages = $("#numOfPages").val();
			if ((numberOfPages > 10) && (page > 10)) {
				var pageUrl = urlBase + (startDisplayPage - 10) + "";
				pager += "<li class=\"prev pager_nav pager_item\" data-page=\"" + pageUrl + "\"><a>Prev 10</a></li>\n";
			}
			if (page != 1) {
				var pageUrl = urlBase + (page - 1) + "";
				pager += "<li class=\"prev pager_nav pager_item\" data-page=\"" + pageUrl + "\"><a>Prev</a></li>\n";
			}
			
			console.log("pages: " + numberOfPages);
			console.log("last display page: " + (startDisplayPage + 9));
			var end;
			if (startDisplayPage + 9 >= numberOfPages) {
				end = numberOfPages;
			}
			else {
				end = startDisplayPage + 9;
			}
			for (var i = startDisplayPage; i <= end; i++) {
				if (i == page) {
					pager += "<li class=\"active\"><a>" + i + "</a></li>";
				}
				else {
					var pageUrl = urlBase + i + "";
					pager += "<li class=\"pager_item\" data-page=\"" + pageUrl + "\"><a>" + i + "</a></li>\n";
				}
			}
			if (page != numberOfPages) {
				var pageUrl = urlBase + (page + 1) + "";
				pager += "<li class=\"next pager_nav pager_item\" data-page=\"" + pageUrl + "\"><a>Next</a></li>\n";
			}
			
			if (Math.floor(page / 10) < Math.floor(numberOfPages / 10)) {
				var pageUrl = urlBase + (startDisplayPage + 10) + "";
				pager += "<li class=\"next pager_nav pager_item\" data-page=\"" + pageUrl + "\"><a>Next 10</a></li>\n";
			}
			console.log("PAGER: " + pager);
			$("#pagination").html(pager);
			
			
		}).fail (function(err) {
		    console.error(err);
		       
		});
		
	});
	
	
});