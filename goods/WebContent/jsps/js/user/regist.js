
$(function(){
	//1.获取所有错误信息，直接触发的函数
	$(".errorClass").each(function(){
		showError($(this));
	});
	
	//2.提交按钮移出和移进来切换图片
	$("#submitBtn").hover(
	function(){//移进
		$("#submitBtn").attr("src","/goods/images/regist2.jpg");
	},
	function(){//移出
		$("#submitBtn").attr("src","/goods/images/regist1.jpg");
	}
	);
	
	//3.输入框得到焦点隐藏错误信息
	$(".inputClass").focus(function(){
			var labelId = $(this).attr("id") + "Error";
			$("#"+labelId).text("");
			showError($("#"+labelId))
	});
		
	//4.输入框失去焦点进行校验
	$(".inputClass").blur(function(){
		//获取当前输入框的id
		var id = $(this).attr("id");
		//获取对应校验的方法名
		var funName = "validate"+id.substring(0,1).toUpperCase()+id.substring(1)+"()";
		eval(funName);
	})
	//5.表单提交验证
	$("#registForm").submit(function(){
		var bool = true;
		if(!validateLoginname()){
			bool = false;
		}
		if(!validateLoginpass()){
			bool = false;
		}
		if(!validateReloginpass()){
			bool = false;
		}
		if(!validateEmail()){
			bool = false;
		}
		if(!validateVerifyCode()){
			bool = false;
		}
		return bool;
		
	});
	
});
//校验
function validateLoginname(){
	var id = "loginname";
	var value = $("#"+id).val();//获取文本值
	//非空校验
	if(!value){
		$("#"+id+"Error").text("用户名不能为空");
		showError($("#"+id+"Error"));
		return false;
	}
	//长度校验
	if(value.length < 3 || value.length > 20){
		$("#"+id+"Error").text("用户名长度必须在3-20之间");
		showError($("#"+id+"Error"));
		return false;
	}
	//【后端】ajax校验
	$.ajax({
		url:"/goods/UserServlet",//注意路径一定要写正确
		data:{method:"ajaxValidateLoginname",loginname:value},
		type:"POST",
		dataType:"json",
		async:false,
		cache:false,
		success:function(result){
			if(result){
				$("#"+id+"Error").text("用户名已注册！");
				showError($("#"+id+"Error"));
				return false;
			}
		}
	});
	return true;
}
function validateLoginpass(){
	var id = "loginpass";
	var value = $("#"+id).val();//获取文本值
	//非空校验
	if(!value){
		$("#"+id+"Error").text("密码不能为空");
		showError($("#"+id+"Error"));
		return false;
	}
	//长度校验
	if(value.length < 3 || value.length > 20){
		$("#"+id+"Error").text("密码长度必须在3-20之间");
		showError($("#"+id+"Error"));
		return false;
	}
	
	return true;
}
function validateReloginpass(){
	var id = "reloginpass";
	var value = $("#"+id).val();//获取文本值
	//非空校验
	if(!value){
		$("#"+id+"Error").text("确认密码不能为空");
		showError($("#"+id+"Error"));
		return false;
	}
	//两次密码输入是否一致
	if(value != $("#loginpass").val()){
		$("#"+id+"Error").text("两次输入密码不一致");
		showError($("#"+id+"Error"));
		return false;
	}
	
	return true;
}
function validateEmail(){
	var id = "email";
	var value = $("#"+id).val();//获取文本值
	//非空校验
	if(!value){
		$("#"+id+"Error").text("Email不能为空");
		showError($("#"+id+"Error"));
		return false;
	}
	//Email格式校验
	if(!(/^[a-z\d]+(\.[a-z\d]+)*@([\da-z](-[\da-z])?)+(\.{1,2}[a-z]+)+$/.test(value))){
		$("#"+id+"Error").text("邮箱格式不正确！");
		showError($("#"+id+"Error"));
		return false;
	}
	//【后端】ajax校验
	$.ajax({
		url:"/goods/UserServlet",//注意路径一定要写正确
		data:{method:"ajaxValidateEmail",email:value},
		type:"POST",
		dataType:"json",
		async:false,
		cache:false,
		success:function(result){
			if(result){
				$("#"+id+"Error").text("Email已注册！");
				showError($("#"+id+"Error"));
				return false;
			}
		}
	});
	return true;
}
function validateVerifyCode(){
	var id = "verifyCode";
	var value = $("#"+id).val();//获取文本值
	//非空校验
	if(!value){
		$("#"+id+"Error").text("验证码不能为空");
		showError($("#"+id+"Error"));
		return false;
	}
	//验证码校验
	if(value.length != 4){
		$("#"+id+"Error").text("验证码错误！");
		showError($("#"+id+"Error"));
		return false;
	}
	//【后端】ajax校验
	$.ajax({
		url:"/goods/UserServlet",//注意路径一定要写正确
		data:{method:"ajaxValidateVerifyCode",verifyCode:value},
		type:"POST",
		dataType:"json",
		async:false,
		cache:false,
		success:function(result){
			if(result){
				$("#"+id+"Error").text("验证码错误！");
				showError($("#"+id+"Error"));
				return false;
			}
		}
	});
	return true;
}
//判断当前元素是否存在内容，如果存在显示
function showError(ele){
	var text = ele.text();
	if(!text){
		ele.css("display","none");//隐藏元素
	}else{
		ele.css("display","");//显示元素
	}
}



//换一张验证码
function fyz(){
	//alert("aa");
	$("#imgVerifyCode").attr("src","/goods/VerifyCodeServlet?a="+new Date().getTime());
	//此处加参数a的原因是，浏览器会有缓存，认为此次请求会和第一次打开该页面发出的请求一样，因此要加一个参数，是请求不一样（浏览器缓存）
}
