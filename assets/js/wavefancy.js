

aTime = 500; //animate time

cOpen = null; //current open.
nowOpen = null;

// function closeAndOpen(closeID,openID){
	// alert("OK");
// 	
	// //$(selector).animate(styles,speed,easing,callback)
	// $("#"+closeID).animate({height:"0px"},{ duration: aTime, queue: true }).css({display:"none"});
	// $("#"+openID).css({display:"block"}).animate({height:"100%"},{ duration: aTime, queue: true });
// }

function open(openID){
	if(cOpen != null){
		close(cOpen);
		nowOpen = $("#"+openID);
		//wait previous animation to finish, then start the next one.
		setTimeout("afterOpen()",aTime);
	}else{
		$("#"+openID).css({display:"block",height:"100%","margin-top":"100%"})
		 .animate({"margin-top":"0"},{duration:aTime});
	}
	
	cOpen = $("#"+openID);
}

/**
 * Close displayed element.
 * @param {Object} openE
 */
function close(openedE){
	 openedE.animate({"margin-top":"100%"},{duration:aTime},'linear',function(){
	 	openedE.css({height:"0px",display:"none"});
	 });
        			 // $("#st-panel-1").css({height:"0",display:"none"});
}

function afterOpen(){
	nowOpen.css({display:"block",height:"100%","margin-top":"100%"})
		   .animate({"margin-top":"0"},{duration:aTime});
}

/**
 * Set proper width for page.
 * Fix the bug of a blank line on the right edge.
 */
//function initialPage(){
//	var w = window.screen.width; 
//	$("#container").css({width:w});
//	$("#body").css({width:w});
//}

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

function addNewItem(){ //add or update.
//	alert("here!");
	var q = new Object();
	q.id = $("#addItem").attr("dID"); //add negative add, alter positive id.
	q.question = $("#newItem1").val();
	
	q.options = [];
	
	$("#aList").children().each(function(){
		//alert($(this).text().trim().length);
		var s = $(this).text().trim();
		var o = new Object();
		o.option = s.substring(0,s.length-1);
		o.id = $(this).attr("oID");
//		alert(s.substring(0,s.length-1));
		q.options.push(o);
	});	

	q.repeat_type = $("#add_repeat").val();
	var t = $("#add_time").val().split(":");
	q.hour = t[0];
	q.minute = t[1];
	alert(JSON.stringify(q));
	
	Android.processQuestion(JSON.stringify(q), "create")
	
	alert(Android.getAllQuestions());
	//return (JSON.stringify(q));
}

function getItemByID(){ //get question by ID.
	var result = '{"id":"1","question":"请添加提醒问题","options":[{"option":"是1 ","id":"1"},{"option":"否 ","id":"2"}],"repeat_type":"2","hour":"13","minute":"15"}';
	
	var q = eval('('+result+')');
	//alert(q.question);
	$("#addItem").attr("dID",q.id);
	$("#newItem1").val(q.question);
	$("#add_repeat").val(q.repeat_type);
	$("#add_time").val(q.hour + ":" + q.minute);
	
	$("#aList").empty();
	for(var i in q.options){
		//alert(q.options[i].id);
		var e = '<div class="ndel" oID="'+q.options[i].id+'"><span class="bLine" > '+q.options[i].option+' <span class="RaphaelIcons textGray" style="display: inline-table;">Â</span></span></div>'
		$("#aList").append(e);
	}
	//alert("OK");
};

function setCurrentItem(){
	var result = '{"id":"1","question":"请添加提醒问题","options":[{"option":"是1 ","id":"1"},{"option":"否 ","id":"2"}],"repeat_type":"2","hour":"13","minute":"15"}';
	
	var q = eval('('+result+')');
	
	$("#currItem").attr("dID",q.id);
	$("#currItemQuestion").val(q.question);
	
	$("#currItemList").empty();
	for(var i in q.options){
		//alert(q.options[i].id);
		var e = '<div class="currList" oID="'+q.options[i].id+'"><span class="bLine" > '+q.options[i].option+' <span class="RaphaelIcons textGray" style="display: inline-table;">Ã</span></span></div>'
		$("#currItemList").append(e);
	}
	
	//alert($("#delay").val());
	//bind option choose functions.
	bindOptionToActiveItem();
};

function bindOptionToActiveItem(){
	$(".currList").click(function(){
		saveOptionChoose($(this).attr("oID"));
	});
};

function saveOptionChoose(optionID){
	var para = new Object();
	para.id = $("#currItem").attr("dID");
	para.optionID = optionID;
	
	alert(JSON.stringify(para));
}

/**
 * Save delay setting for current active items.
 */
function saveDelay(){
	var para = new Object();
	para.id = $("#currItem").attr("dID");
	para.delay = $("#delay").val();
	
	alert(JSON.stringify(para));
}






