<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html lang="en">
<head>
	<title>Register</title>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">

	<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/images/icons/favicon.ico"/>

	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/vendor/bootstrap/css/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/fonts/font-awesome-4.7.0/css/font-awesome.min.css">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/vendor/animate/animate.css">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/vendor/css-hamburgers/hamburgers.min.css">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/vendor/select2/select2.min.css">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/util.css">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css">
</head>

<body>

<div class="limiter">
	<div class="container-login100">
		<div class="wrap-login100">

			<div class="login100-pic js-tilt" data-tilt>
				<img src="${pageContext.request.contextPath}/images/img-01.png" alt="IMG">
			</div>

			<form:form class="login100-form validate-form"
					   method="post"
					   modelAttribute="user"
					   action="${pageContext.request.contextPath}/auth/doRegister">

				<span class="login100-form-title">
					Sign Up
				</span>

				<!-- CSRF -->
				<input type="hidden"
					   name="${_csrf.parameterName}"
					   value="${_csrf.token}"/>

				<div class="wrap-input100 validate-input">
					<form:input path="email" class="input100" type="email" placeholder="Email"/>
					<span class="focus-input100"></span>
					<span class="symbol-input100">
						<i class="fa fa-envelope"></i>
					</span>
					<form:errors path="email" cssStyle="color:red"/>
				</div>

				<div class="wrap-input100 validate-input">
					<form:input path="username" class="input100" placeholder="Username"/>
					<span class="focus-input100"></span>
					<span class="symbol-input100">
						<i class="fa fa-user"></i>
					</span>
					<form:errors path="username" cssStyle="color:red"/>
				</div>

				<div class="wrap-input100 validate-input">
					<form:password path="password" class="input100" placeholder="Password"/>
					<span class="focus-input100"></span>
					<span class="symbol-input100">
						<i class="fa fa-lock"></i>
					</span>
					<form:errors path="password" cssStyle="color:red"/>
				</div>

				<div class="container-login100-form-btn">
					<button class="login100-form-btn" type="submit">
						Sign Up
					</button>
				</div>

				<div class="text-center p-t-12">
					<a class="txt2" href="${pageContext.request.contextPath}/auth/reset">
						Forgot Username / Password?
					</a>
				</div>

				<div class="text-center p-t-136">
					<a class="txt2" href="${pageContext.request.contextPath}/auth/login">
						Already Have Account? Sign in
					</a>
				</div>

			</form:form>

		</div>
	</div>
</div>

<script src="${pageContext.request.contextPath}/vendor/jquery/jquery-3.2.1.min.js"></script>
<script src="${pageContext.request.contextPath}/vendor/bootstrap/js/popper.js"></script>
<script src="${pageContext.request.contextPath}/vendor/bootstrap/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/vendor/select2/select2.min.js"></script>
<script src="${pageContext.request.contextPath}/vendor/tilt/tilt.jquery.min.js"></script>

<script>
	$('.js-tilt').tilt({
		scale: 1.1
	})
</script>

<script>
	setInterval(() => {
		fetch("${pageContext.request.contextPath}/auth/status")
				.then(r => r.json())
				.then(ok => {
					if (ok) window.location = "${pageContext.request.contextPath}/home";
				});
	}, 1000);
</script>

<script src="${pageContext.request.contextPath}/js/main.js"></script>

</body>
</html>