<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
	<title>Login V1</title>
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

			<form class="login100-form validate-form"
				  method="post"
				  action="${pageContext.request.contextPath}/doReset">

				<span class="login100-form-title">
					Get Account Details
				</span>

				<!-- 🔧 CSRF token -->
				<input type="hidden"
					   name="${_csrf.parameterName}"
					   value="${_csrf.token}"/>

				<div class="wrap-input100 validate-input" data-validate = "Valid email is required: ex@abc.xyz">
					<input class="input100" type="email" name="email" placeholder="Email">
					<span class="focus-input100"></span>
					<span class="symbol-input100">
						<i class="fa fa-envelope"></i>
					</span>
				</div>

				<div class="container-login100-form-btn">
					<button class="login100-form-btn" type="submit">
						Set Verification To Email
					</button>
				</div>

				<div class="text-center p-t-136">
                	<a class="txt2" href="${pageContext.request.contextPath}/auth/login">
                	    Go Back
                	</a>
                </div>
			</form>
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
  fetch("/auth/status")
    .then(r => r.json())
    .then(ok => {
      if (ok) window.location = "/auth/";
    });
}, 1000);
</script>

<script src="${pageContext.request.contextPath}/js/main.js"></script>

</body>
</html>
