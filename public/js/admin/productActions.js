
var deleteProductId = '';
var productPanel = new Object();

$(document).ready(function() {
	
	$(document).on('click', '#confirmAddProduct', function (e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data("url");
		var name = $(this).closest('.modal-content').find('[name="name"]').val();
		var price = $(this).closest('.modal-content').find('[name="price"]').val();
		var salePrice = $(this).closest('.modal-content').find('[name="salePrice"]').val();
		var trialPeriod = $(this).closest('.modal-content').find('[name="trialPeriod"]').val();
		
		var form = $(this).closest('.modal-content');
		
		if (!validateProduct(this)) {
			return;
		}
		
		var productJSON =  {'name': name, 'price': price, 'salePrice': salePrice, 'trialPeriod': trialPeriod};
		
		console.log(url);
		console.log(productJSON);
		
		$.ajax({
			
	        type: 'POST',
	        url: url, 
	        contentType: 'application/json',
	        data: JSON.stringify(productJSON),
	        dataType: 'json'
		
	    }).done (function(data) {
	    	
	    	console.log(data);
	    	
	    	showSuccessAlert(data.message);
	    
	    	$('#add-product').modal('toggle');
	    	
	    	//display new product
	    	var id = data.id;
	    	
	    	var productHtml = $('.panel').html();
	    	productHtml = '<div class="panel panel-default">\n' + productHtml + "\n</div>";
	    	
	    	var index = parseInt($('.index:last').text()) + 1;
	    	
	    	$('#accordion').append(productHtml);
	    	
	    	console.log("index: " + index);
	    	$('.panel-title-text:last').prop('href', '#collapse' + (index - 1));
	    	$('.collapse:last').prop('id', 'collapse' + (index - 1));
	    	$('.panel-title-text:last').html("<span class=\"index\">" + 
	    			index + "</span>. " + name);
	    	$('.productID:last').text(id);
	    	
	    	$('.name').eq(-2).val(name);
	    	
	    	$('.price').eq(-2).val(price);
	    	$('.sale-price').eq(-2).val(salePrice);
	    	$('.trial-period').eq(-2).val(trialPeriod);
	    	
	    	
	    }).fail (function(err) {
			
	    	console.error(err);
			processErrors(err, form);
			
	    });
	});
	
	$(document).on('click', '#editProduct', function (e) {
		
		e.preventDefault();
		
		clearErrors();
		
		var url = $(this).data("url");
		
		var id = $(this).closest('.edit-form').find('.productID').text();
		var name = $(this).closest('.edit-form').find('[name="name"]').val();
		var price = $(this).closest('.edit-form').find('[name="price"]').val();
		var salePrice = $(this).closest('.edit-form').find('[name="salePrice"]').val();
		var trialPeriod = $(this).closest('.edit-form').find('[name="trialPeriod"]').val();
		
		var form = $(this).closest('.edit-form');
		
		if (!validateProduct($(this))) {
			return;
		}
		
		var productJSON =  {'id': id, 'name': name, 'price': price, 'salePrice': salePrice, 'trialPeriod': trialPeriod};
		
		console.log(url);
		console.log(productJSON);
		
		$.ajax({
			
	        type: 'POST',
	        url: url, 
	        contentType: 'application/json',
	        data: JSON.stringify(productJSON),
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
	
	$('#deleteProduct').on('show.bs.modal', function (e) {
	    var trigger = $(e.relatedTarget);
	    var id = $(trigger).closest('.edit-form').find('.productID').text();
	    
	    deleteProductId = id;
	    productPanel = $(trigger).closest('.panel');
	});
	
	$(document).on('click', '#confirmDeleteProduct', function (e) {
		
		e.preventDefault();
		
		var url = $(this).data("url");
		
		var id = deleteProductId;
		var panel = productPanel;
		
		$.ajax({
			
	        type: 'POST',
	        url: url,
	        data: {"id": id},
	        dataType: 'json'
		
	    }).done (function(data) {
	    	
	    	console.log(data);

	    	showSuccessAlert(data.message);
	    	
	    	$(panel).remove();
	    	
	    	$('#deleteProduct').modal('toggle');
	    	
	    	$("html, body").animate({ scrollTop: 0 }, "slow");
	    	
	    	
	    
	    }).fail (function(err) {
			
	    	console.error(err)
			
	    });
		
	});
	

	function validateProduct(ref) {
		
		var validated = true;
		$(ref).closest('.validate-product').find('.form-input').each(function(i, obj) {
			
			var val = $(obj).val();
			if (val == "") {
				var errorsHtml = $(obj).parent().find('.error').html();
				
				errorsHtml += "Field is required" + "</br>";
				
				$(obj).parent().find('.error').html(errorsHtml);
				validated = false;
			}
			
		});
		
		var price = $(ref).closest('.validate-product').find('[name="price"]').val();
		
		if(!price.match(/^-?(\d*\.)?\d*$/)) {
			var errorsHtml = $(ref).closest('.validate-product').find('.price-error').html();
			errorsHtml += "Price should be numeric value" + "</br>";
			$(ref).closest('.validate-product').find('.price-error').html(errorsHtml);
			validated = false;
		
		}
		else if (price < 0) {
			var errorsHtml = $(ref).closest('.validate-product').find('.price-error').html();
			errorsHtml += "Price should be gteater than 0" + "</br>";
			$(ref).closest('.validate-product').find('.price-error').html(errorsHtml);
			validated = false;
		}
		
		var salePrice = $(ref).closest('.validate-product').find('[name="salePrice"]').val();
		
		if(!salePrice.match(/^-?(\d*\.)?\d*$/)) {
			var errorsHtml = $(ref).closest('.validate-product').find('.salePrice-error').html();
			errorsHtml += "Sale Price should be numeric value" + "</br>";
			$(ref).closest('.validate-product').find('.salePrice-error').html(errorsHtml);
			validated = false;
		}
		else if (salePrice < 0) {
			var errorsHtml = $(ref).closest('.validate-product').find('.salePrice-error').html();
			errorsHtml += "Sale Price should be greater than 0" + "</br>";
			$(ref).closest('.validate-product').find('.salePrice-error').html(errorsHtml);
			validated = false;
		}
		
		var trialPeriod = $(ref).closest('.validate-product').find('[name="trialPeriod"]').val();
		
		if(!trialPeriod.match(/^-?(\d*\.)?\d*$/)) {
			var errorsHtml = $(ref).closest('.validate-product').find('.trialPeriod-error').html();
			errorsHtml += "Trial Period should be numeric value" + "</br>";
			$(ref).closest('.validate-product').find('.trialPeriod-error').html(errorsHtml);
			validated = false;
		}
		else if (trialPeriod < 0) {
			var errorsHtml = $(ref).closest('.validate-product').find('.trialPeriod-error').html();
			errorsHtml += "Trial Period should be greater than 0" + "</br>";
			$(ref).closest('.validate-product').find('.trialPeriod-error').html(errorsHtml);
			validated = false;
		}
		return validated;
	}
	
});