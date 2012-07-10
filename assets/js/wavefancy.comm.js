
aTime=600;
oTime=600;

var pURL = null; 

/**
 * This function will be called by the bottom panel.
 * In order to add a animation for page close.
 * 
 * This function also need to be call when want to transite to a another page.
 */
function topLoad(URL){
	pURL = URL;
	$("#move").animate({"margin-top":"150%"},{duration:aTime})
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
	$("#move").css({"margin-top":"150%"}).animate({"margin-top":"0"},{duration:aTime});
}

/**
 * Get query string through regular expression.
 */
function getQueryStringRegExp(name) 
{ 
    var reg = new RegExp("(^|\\?|&)"+ name +"=([^&]*)(\\s|&|$)", "i");   
    if (reg.test(location.href)) return unescape(RegExp.$2.replace(/\+/g, " ")); return ""; 
};

/*
 * Check is the value in array.
 */
function contains(v,array){
	for(var i=0;i<array.length;i++){
		if(v == array[i]){
			return true;
		}
	}
	
	return false;
}


//--------------------Functions for Block UI---------------------------//
/**
 * unblockUI whit delay time.
 * @param {Object} delayTime
 */
function unBlockUI(delayTime){
	setTimeout($.unblockUI,delayTime);	
}

/**
 * Block UI
 * @param message
 */
function blockUI(message){
	
	 $.blockUI({message: message , 
		  
		 css: { 
          border: 'none', 
          padding: '15px', 
          backgroundColor: '#000', 
          '-webkit-border-radius': '10px', 
          '-moz-border-radius': '10px', 
          opacity: .5, 
          color: '#fff' 
	 } });
}

/**
 * Block UI no overlay
 * @param message
 */
function blockUINoOverlay(message){
	
	 $.blockUI({message: message , 
	 	
		 showOverlay:false,  
		 css: { 
         border: 'none', 
         padding: '15px', 
         backgroundColor: '#000', 
         '-webkit-border-radius': '10px', 
         '-moz-border-radius': '10px', 
         opacity: .9, 
         color: '#fff' 
	 } });
}
//--------------------Functions for Block UI END---------------------------//

