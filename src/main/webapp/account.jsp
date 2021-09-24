<html>
<head>
	<title>View Account "<%= request.getParameter("account")%>"</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
	<script>
	function addTransaction(){
		var t = "{\"date\":\"" + $("#date").val() + "\","
			+ "\"ref\":\"" + $("#ref").val() + "\","
			+ "\"payee\":\"" + $("#payee").val() + "\","
			+ "\"category\":\"" + $("#category").val() + "\","
			+ "\"memo\":\"" + $("#memo").val() + "\","
			+ "\"cleared\":" + ($("#cleared").prop("checked") ? "true" : "false") + ","
			+ "\"cents\":" + 100*$("#cents").val() + "}";

		$.ajax({
			url: "account/<%= request.getParameter("account")%>/add",
			data: t,
			method: "POST",
			contentType: "application/json"
		})
		.done(function(){
			location.reload();
		})
		.fail(function(data, textStatus, jqXHR){
			alert("Could not add transaction" + jqXHR);
		});
	}

	$(document).ready(function(){
		$.getJSON( "account/<%= request.getParameter("account")%>/transactions", function( data ) {
			var items = [];
			$.each( data, function( index, transaction ) {
				items.push("<tr><td>" + transaction.date + "</td>"
						+ "<td>" + transaction.ref + "</td>"
						+ "<td>" + transaction.payee + "</td>"
						+ "<td>" + transaction.category + "</td>"
						+ "<td>" + transaction.memo + "</td>"
						+ "<td>" + (transaction.cleared ? "true" : "false") + "</td>"
						+ "<td>" + transaction.cents/100 + "</td></tr>");
			});
			$("#transactions").append(items.join(""));
		});		
	});
	</script>
</head>
<body>
<table id="transactions">
<tr>
	<th>Date</th>
	<th>Ref</th>
	<th>Payee</th>
	<th>Category</th>
	<th>Memo</th>
	<th>Cleared</th>
	<th>Amount ($)</th>
</tr>
<tr>
	<td><input type="date" id="date"></td>
	<td><input type="text" id="ref"></td>
	<td><input type="text" id="payee"></td>
	<td><input type="text" id="category"></td>
	<td><input type="text" id="memo"></td>
	<td><input type="checkbox" id="cleared"></td>
	<td><input type="number" id="cents"></td>
</tr>
<tr>
<td><input type="button" value="Add" onClick="addTransaction();">
</tr>
</table>
</body>
</html>