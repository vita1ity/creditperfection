//remove error messages
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

function updatePagination(urlBase, pageStr, numberOfPages) {
	
	var page = parseInt(pageStr);
	
	console.log("urlBase: " + urlBase + ", page: " + page);
	
	$('#currentPage').val(page);
	var startDisplayPage = parseInt($('#startDisplayPage').val());
	console.log("startDisplayPage: " + startDisplayPage);
	//console.log(startDisplayPage / 10);
	
	if ((Math.floor((page - 1) / 10))  != (Math.floor(startDisplayPage / 10))) {
		
		startDisplayPage = Math.floor((page - 1) / 10) * 10 + 1;
		$('#startDisplayPage').val(startDisplayPage);
		console.log("start display page changed: " + startDisplayPage);
	}
	
	$('#pageUrl').val(urlBase + page);
	
	console.log("Page url: " + $('#pageUrl').val());
	
	var pager = "";
	
	
	console.log(page);
	
	$("#numOfPages").val(numberOfPages);
	
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
	if (numberOfPages != 0 && page != numberOfPages) {
		var pageUrl = urlBase + (page + 1) + "";
		pager += "<li class=\"next pager_nav pager_item\" data-page=\"" + pageUrl + "\"><a>Next</a></li>\n";
	}
	
	if (Math.floor(page / 10) < Math.floor(numberOfPages / 10)) {
		var pageUrl = urlBase + (startDisplayPage + 10) + "";
		pager += "<li class=\"next pager_nav pager_item\" data-page=\"" + pageUrl + "\"><a>Next 10</a></li>\n";
	}
	$("#pagination").html(pager);
	
}