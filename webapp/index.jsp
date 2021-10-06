<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Flow chart generator</title>

<script src="mermaid.min.js"></script>
<link rel="stylesheet"
	href="bootstrap.min.css">
<script
	src="bootstrap.min.js"></script>


<script>
	mermaid.initialize({
		startOnLoad : true
	});
</script>
</head>
<body style="background-color: #fef9ef">
	<div class="container-fluid row p-3">
		<div class="col-12 mb-4"
			style="text-align: center; border-bottom: 2px solid black">
			<h1>Flowchart Generator</h1>
		</div>
		<div class="col-4">
			<form method="post" action="Convertor">
				<textarea style="background-color: #002240; color: #FFFFFF"
					id="code" name="code" cols="40" rows="25" >${tempCode}</textarea>
				<br>
				<button type="submit" class="btn btn-primary" id="submit"
					value="Run Code">Run Code</button>
			</form>
		</div>
		<div class="col-2"></div>
		<div class="mermaid col-6" id="solution">${solution}</div>
	</div>
</body>
</html>


