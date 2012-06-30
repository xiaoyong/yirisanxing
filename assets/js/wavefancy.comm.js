
aTime=600;

var pURL = null; 

/**
 * This function will be called by the bottom panel.
 * In order to add a animation for page close.
 */
function topLoad(URL){
	pURL = URL;
	$("#move").animate({"margin-top":"100%"},{duration:aTime})
	// this.location.href=URL;
	setTimeout("pageLoad()",aTime);
}

function pageLoad(){
	this.location.href=pURL;
}


/**
 * animation for open page
 */
function openPage(){
	$("#move").animate({"margin-top":"0"},{duration:aTime});
}
