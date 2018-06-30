var E3MALL = {
	checkLogin : function(){
		var _ticket = $.cookie("e3token");
		if(!_ticket){
			return ;
		}
		$.ajax({
			url : "http://localhost:8088/user/token/" + _ticket,
			dataType : "jsonp",
			type : "GET",
			success : function(data){
				if(data.status == 200){
					var username = data.data.username;
					var userId=data.data.id;
					var html = username + "，欢迎来到宜立方购物网！<a href=\"http://localhost:8088/user/logout/"+_ticket+".html\" class=\"link-logout\">[退出]</a>";
					$("#loginbar").html(html);
				}
			}
		});
	}
}

$(function(){
	// 查看是否已经登录，如果已经登录查询登录信息
	E3MALL.checkLogin();
});